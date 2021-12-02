package logic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.google.common.collect.Lists;

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
		List<String> allBranches = getRemoteBranchesNames();
		changeBranch("master");
		for(String branch: allBranches)
			deleteBranch(branch);
	}
	
	public void changeBranch(String branchName) {
		try {
			git.checkout().setCreateBranch(false).setName(branchName).call();
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
	public void commit(String message) {
		try {
			git.commit().setAll(true).setMessage(message).call();
		} catch (GitAPIException e) {
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
	
	public void commitAndPush(String commitMsg, String branchName) {
		commit(commitMsg);
		push();
		publishBranch(branchName);
	}
	
	public List<Ref> getRemoteBranches() {
		try {
			List<Ref> branches = git.branchList().setListMode(ListMode.REMOTE).call();
			branches.removeIf(r -> r.getName().equals("refs/remotes/origin/HEAD"));
			return branches;
		} catch (GitAPIException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getRemoteBranchesNames() {
		List<String> names = new ArrayList<>();
		List<Ref> branches = getRemoteBranches();
		branches.forEach(r -> names.add(r.getName().replace("refs/remotes/origin/", "")));
		names.remove("master");
		return names;
	}
	
	public String getNextBranchName(String email) {
		List<String> branchesNames = getRemoteBranchesNames();
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
	
	public HashMap<String, RevCommit> getBranchesLastCommit() {
		try {
			HashMap<String, RevCommit> branchesLastCommit = new HashMap<>();
			List<Ref> branches = getRemoteBranches();
			for (Ref branch : branches) {
				String treeName = branch.getName();
				List<RevCommit> commits = Lists.newArrayList(git.log().add(repository.resolve(treeName)).call().iterator());
				branchesLastCommit.put(treeName.replace("refs/remotes/origin/", ""), commits.get(0));
			}
			return branchesLastCommit;
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getCommitDiff(String branch) {
		try {
			HashMap<String, RevCommit> branchesLastCommit = getBranchesLastCommit();
			RevCommit oldCommit = branchesLastCommit.get("master");
			RevCommit newCommit = branchesLastCommit.get(branch);
		    ObjectReader reader = repository.newObjectReader();
		    AbstractTreeIterator oldTreeIterator = new CanonicalTreeParser(null, reader, oldCommit.getTree().getId());	
		    AbstractTreeIterator newTreeIterator = new CanonicalTreeParser(null, reader, newCommit.getTree().getId());
		    OutputStream outputStream = new ByteArrayOutputStream();
		    DiffFormatter formatter = new DiffFormatter(outputStream);
		    formatter.setRepository(repository);
		    formatter.format(oldTreeIterator, newTreeIterator);
		    return outputStream.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
