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
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
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
			RefUpdate newBranch = createBranch(branchName);
//			changeBranch(branchName);
			
			commit("Testing branch again " + branchName);
			push("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");
			
//			changeBranch("master");
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
}
