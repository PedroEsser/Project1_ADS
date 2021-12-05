package logic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.storage.file.LockFile;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;

public class GitHandler {

	private final static CredentialsProvider CREDENTIALS = new UsernamePasswordCredentialsProvider("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", "");
	private Repository repository;
	private Ref master;
	private Git git;
	
	public GitHandler(String gitDir) {
		try {
			repository = new FileRepositoryBuilder().setGitDir(new File(gitDir)).build();
			master = repository.getRef("master");
			git = new Git(repository);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void test(String branchName) {
		try {
			RefUpdate newBranch = createBranch(branchName);
			changeBranch(branchName);
			
			commit("Testing branch again " + branchName);
			push();
			publishBranch(branchName);
			changeBranch("master");
//			mergeBrach(branchName);
//			push("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");
//			deleteBranch(branchName);
		} catch (Exception e) {
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
			File file = new File(repository.getWorkTree(), "ontology.owl");
		    LockFile l = new LockFile(file, FS.DETECTED);
			return new OWLHandler(inputStream, l);
		} catch (RevisionSyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public RefUpdate createBranch(String branchName) { // if user already has branch, set name of branch to branch_n
		try {
			RefUpdate branch = repository.updateRef("refs/heads/" + branchName);
			branch.setNewObjectId(master.getObjectId());
			branch.update();
			return branch;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void publishBranch(String branchName){
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

	public void deleteBranch(String branchToDelete) {
		try {
			git.branchDelete().setForce(true).setBranchNames(branchToDelete).call();
			
			RefSpec refSpec = new RefSpec()
			        .setSource(null)
			        .setDestination("refs/heads/" + branchToDelete);
			git.push().setCredentialsProvider(CREDENTIALS).setRefSpecs(refSpec).setRemote("origin").call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	public void deleteAllBranches() {
		List<String> allBranches = getRemoteBranches();
		changeBranch("master");
		for(String branch: allBranches)
			deleteBranch(branch);
	}
	
	public void changeBranch(String branchName) {
		try {
			git.checkout().setForce(true).setCreateBranch(false).setName(branchName).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void mergeBrach(String branchName) {
		try {
			MergeCommand mgCmd = git.merge();
			mgCmd.include(repository.getRef(branchName)).setCommit(true).setMessage("Merging branch " + branchName + " into master, i hope :)");
			
			MergeResult res = mgCmd.call();
		
			if (res.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)){
			   System.out.println(res.getConflicts().toString());
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void push() {			// Pushes to current branch
		try {
			git.push().setCredentialsProvider(CREDENTIALS).call();
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
	
	public List<String> getRemoteBranches() {
		List<String> branchesNames = new ArrayList<>();
		try {
			List<Ref> call = new Git(repository).branchList().setListMode(ListMode.REMOTE).call();
			for (Ref ref : call) {
				branchesNames.add(ref.getName().replace("refs/remotes/origin/", ""));
			}
			branchesNames.remove("HEAD");
			branchesNames.remove("master");
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return branchesNames;
	}
	
	public String getNextBranchName(String email) {
		List<String> branchesNames = getRemoteBranches();
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
	
	public void createAndChangeBranch(String branchName) {
		createBranch(branchName);
		changeBranch(branchName);
	}
	
	public void commitAndPush(String commitMsg, String branchName) {
		commit(commitMsg);
		push();
		publishBranch(branchName);
		changeBranch("master");
	}
}
