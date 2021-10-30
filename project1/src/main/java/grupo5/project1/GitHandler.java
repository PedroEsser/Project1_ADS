package grupo5.project1;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitHandler {

	private Repository repository;
	private Ref master;
	
	public GitHandler(String gitDir) {//"C:\\Users\\pedro\\git\\Project1_ADS\\.git"
		try {
			repository = new FileRepositoryBuilder()
				    .setGitDir(new File(gitDir))
				    .build();
			master = repository.getRef("master");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void test() {
		
		try {
			RefUpdate branch = createBranch("branch1");
			
			//Git.wrap(repository).checkout().setCreateBranch(false).setName(branch.getName()).call();
			Git.wrap(repository).checkout().setCreateBranch(false).setName("master").call();
		
			//deleteBranch(branch.getName());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}
	
	public RefUpdate createBranch(String branchName) {		//if user already has branch, set name to branch_n
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
	
	public void deleteBranch(String branchName) {		//if user already has branch, set name to branch_n
		try {
			RefUpdate deleteBranch1 = repository.updateRef("refs/heads/" + branchName);
			deleteBranch1.setForceUpdate(true);
			deleteBranch1.delete();
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
	
	/*
	 * 
	 * 
	 * // Get a reference
Ref master = repo.getRef("master");

// Get the object the reference points to
ObjectId masterTip = master.getObjectId();

// Rev-parse
ObjectId obj = repo.resolve("HEAD^{tree}");

// Load raw object contents
ObjectLoader loader = repo.open(masterTip);
loader.copyTo(System.out);

// Create a branch
RefUpdate createBranch1 = repo.updateRef("refs/heads/branch1");
createBranch1.setNewObjectId(masterTip);
createBranch1.update();

// Delete a branch
RefUpdate deleteBranch1 = repo.updateRef("refs/heads/branch1");
deleteBranch1.setForceUpdate(true);
deleteBranch1.delete();

// Config
Config cfg = repo.getConfig();
String name = cfg.getString("user", null, "name");
	 * 
	 */
}
