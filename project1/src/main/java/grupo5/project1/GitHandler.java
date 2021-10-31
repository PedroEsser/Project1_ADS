package grupo5.project1;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitHandler {

	private Repository repository;
	private Ref master;

	public GitHandler(String gitDir) {
		try {
			repository = new FileRepositoryBuilder().setGitDir(new File(gitDir)).build();
			master = repository.getRef("master");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void test(String branchName) {
		try {
			//RefUpdate newBranch = createBranch(branchName);
			changeBranch(branchName);
//			push("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");
			
			commit("Testing branch " + branchName);
			
			changeBranch("master");
			mergeBrach(branchName);
			deleteBranch(branchName);
			push("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");
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

	public void deleteBranch(String branchName) {
		try {
			RefUpdate deleteBranch = repository.updateRef("refs/heads/" + branchName);
			deleteBranch.setForceUpdate(true);
			deleteBranch.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeBranch(String branchName) {
		try {
			Git.wrap(repository).checkout().setCreateBranch(false).setName(branchName).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void mergeBrach(String branchName) {
		try {
			MergeCommand mgCmd = Git.wrap(repository).merge();
			mgCmd.include(repository.getRef(branchName)).setCommit(true).setMessage("Merging branch " + branchName + " into master, i hope :)");
			
			MergeResult res = mgCmd.call();
		
			if (res.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)){
			   System.out.println(res.getConflicts().toString());
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
	}

	public void push(String token) {
		try {
			Git.wrap(repository).push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public void commit(String message) {
		try {
			Git.wrap(repository).commit().setAll(true).setMessage(message).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

}
