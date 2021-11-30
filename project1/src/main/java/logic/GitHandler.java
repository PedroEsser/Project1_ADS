package logic;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitHandler {

	private final static String TOKEN_KEY = "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";
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
	
	public void publishBranch(String branchName) throws InvalidRemoteException, TransportException, GitAPIException {
		System.out.println("Publishing branch " + branchName);
		git.push()
		.setCredentialsProvider(new UsernamePasswordCredentialsProvider("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", ""))
	    .setRemote("origin")
	    .setRefSpecs(new RefSpec(branchName + ":" + branchName))
	    .call();
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

	public void push() {			// Pushes to current branch
		try {
			git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(TOKEN_KEY, "")).call();
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

	private List<String> getGitBranches() {
		List<String> branchesNames = new ArrayList<>();
		try {
			List<Ref> call = new Git(repository).branchList().setListMode(null).call();
			for (Ref ref : call) {
				branchesNames.add(ref.getName().replace("refs/heads/", ""));
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return branchesNames;
	}
	
	public String getNextBranchName(String email) {
		List<String> branchesNames = getGitBranches();
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
	
	public void commitAndPush(String commitMsg) {
		commit(commitMsg);
		push();
		changeBranch("master");
	}
}
