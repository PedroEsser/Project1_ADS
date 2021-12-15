package logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.storage.file.LockFile;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;

import com.google.common.collect.Lists;

public class GitHandler {
	
	private static final GitHandler DEFAULT_HANDLER = new GitHandler("./knowledge_base/.git");

	private final static CredentialsProvider CREDENTIALS = new UsernamePasswordCredentialsProvider("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", "");
	private Repository repository;
	private Git git;

	public static GitHandler getDefault() {
		return DEFAULT_HANDLER;
	}
	
	private GitHandler(String gitDir) {
		try {
			repository = new FileRepositoryBuilder().setGitDir(new File(gitDir)).build();
			git = new Git(repository);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public OWLHandler getOWLHandler() {
		try {
			ObjectId id = repository.resolve("HEAD^{tree}");
			TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), "ontology.owl", id);
			ObjectId blobId = treeWalk.getObjectId(0);
			ObjectReader reader = repository.newObjectReader();
		    ObjectLoader result = reader.open(blobId);
		    InputStream inputStream = new ByteArrayInputStream(result.getBytes());
			return new OWLHandler(inputStream, getOntologyFile());
		} catch (RevisionSyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public LockFile getOntologyFile() {
		File file = new File(repository.getWorkTree(), "ontology.owl");
		return new LockFile(file, FS.DETECTED);
	}
	
	public String getOntologyFileContent() {
		try {
			return new String(FileUtils.readFileToByteArray(new File(repository.getWorkTree(), "ontology.owl")));
		} catch (NoWorkTreeException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void writeOntologyFile(String content) {
		LockFile file = getOntologyFile();
		try {
			if(file.lock()) {
				file.write(content.getBytes());
				if(!file.commit())
					System.out.println("Error writting to file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			file.unlock();
		}
	}

	public void createBranch(String branchName) { // if user already has branch, set name of branch to branch_n
		try {
			git.branchCreate().setName(branchName).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void checkoutBranch(String branchName) {
		try {
			git.checkout().setCreateBranch(false).setName(branchName).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void createAndCheckoutBranch(String branchName) {
		createBranch(branchName);
		checkoutBranch(branchName);
	}
	
	public void publishBranch(String branchName) {
		try {
			git.push()
				.setCredentialsProvider(CREDENTIALS)
				.setRemote("origin")
				.setRefSpecs(new RefSpec(branchName + ":" + branchName))
				.call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public String mergeBranch(String branchName) {
		try {
			MergeResult result = git.merge().include(repository.getRef(branchName)).call();
			if (result.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
				return result.getConflicts().toString();
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteBranch(String branchToDelete) {
		try {
			git.branchDelete().setForce(true).setBranchNames(branchToDelete).call();
			git.push()
				.setCredentialsProvider(CREDENTIALS)
				.setRemote("origin")
				.setRefSpecs(new RefSpec().setSource(null).setDestination("refs/heads/" + branchToDelete))
				.call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void add() {
		try {
			git.add().addFilepattern(".").call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void commit(String message) {
		try {
			git.commit().setAll(true).setMessage(message).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	// Pushes to current branch
	public void push() {
		try {
			git.push().setCredentialsProvider(CREDENTIALS).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void commitAndPush(String commitMsg, String branchName) {
		commit(commitMsg);
		push();
		publishBranch(branchName);
	}
	
	public List<Ref> getAllBranches() {
		try {
			List<Ref> branches = git.branchList().setListMode(ListMode.REMOTE).call();
			branches.removeIf(r -> r.getName().equals("refs/remotes/origin/HEAD") || r.getName().equals("refs/remotes/origin/master"));
			return branches;
		} catch (GitAPIException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllBranchesNames() {
		List<String> names = new ArrayList<>();
		List<Ref> branches = getAllBranches();
		branches.forEach(r -> names.add(r.getName().replace("refs/remotes/origin/", "")));
		return names;
	}

	public void deleteAllBranches() {
		List<String> allBranches = getAllBranchesNames();
		checkoutBranch("master");
		for(String branch: allBranches) {
			deleteBranch(branch);
		}
	}
	
	public HashMap<String, Tuple<RevCommit, RevCommit>> getAllBranchesLastCommits() {
		try {
			HashMap<String, Tuple<RevCommit, RevCommit>> branchesLastCommits = new HashMap<>();
			List<Ref> branches = getAllBranches();
			for (Ref branch : branches) {
				String branchName = branch.getName();
				List<RevCommit> commits = Lists.newArrayList(git.log().add(repository.resolve(branchName)).call().iterator());
				branchesLastCommits.put(branchName.replace("refs/remotes/origin/", ""), new Tuple<RevCommit, RevCommit>(commits.get(1), commits.get(0)));
			}
			return branchesLastCommits;
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public HashMap<String, String> getAllBranchesCommitDiff() {
		HashMap<String, String> branchesCommitDiff = new HashMap<>();
		HashMap<String, Tuple<RevCommit, RevCommit>> branchesLastCommit = getAllBranchesLastCommits();
		branchesLastCommit.forEach((branch, commits) -> {
			try {
			    ObjectReader reader = repository.newObjectReader();
			    AbstractTreeIterator oldTreeIterator = new CanonicalTreeParser(null, reader, commits.x.getTree().getId());	
			    AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, reader, commits.y.getTree().getId());
			    OutputStream outputStream = new ByteArrayOutputStream();
			    DiffFormatter formatter = new DiffFormatter(outputStream);
			    formatter.setRepository(repository);
			    formatter.format(oldTreeIterator, newTreeIterator);
			    branchesCommitDiff.put(branch, outputStream.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return branchesCommitDiff;
	}
	
	public String getNextBranchName(String email) {
		List<String> branchesNames = getAllBranchesNames();
		int emailNumber = 0;
		for(String name: branchesNames) {
			if(!name.equals("master") && name.contains(email)) {
				int number = Integer.parseInt(name.replace( email + "_", ""));
				if(number >= emailNumber) {
					emailNumber = number + 1;
				}
			}
		}
		return email + "_" + emailNumber;
	}
	
	private class Tuple<X, Y> { 
		public final X x; 
		public final Y y; 
		public Tuple(X x, Y y) { 
			this.x = x; 
			this.y = y; 
		} 
	}
	
}