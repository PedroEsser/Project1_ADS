package grupo5.project1;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

	public void test() {
		try {
			RefUpdate branch = createBranch("branch1");

			changeBranch(branch.getName());
			changeBranch("master");

			deleteBranch("branch1");
		} catch (Exception e1) {
			e1.printStackTrace();
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

	public void push(String token) {
		try {
			Git.wrap(repository).push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

}
