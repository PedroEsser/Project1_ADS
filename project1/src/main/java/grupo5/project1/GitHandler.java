package grupo5.project1;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitHandler {

	Repository repository;
	
	public GitHandler(String gitDir) {//"C:\\Users\\pedro\\git\\Project1_ADS\\.git"
		try {
			repository = new FileRepositoryBuilder()
				    .setGitDir(new File(gitDir))
				    .build();
		} catch (IOException e) {
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
