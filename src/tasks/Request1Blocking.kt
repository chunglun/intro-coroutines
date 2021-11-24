package tasks

import contributors.*
import retrofit2.Response

/**
 * execute() us a synchronous call which blocks the underlying thread.
 *
 * The window will freeze and won't react to input until the loading is finished.
 *
 * All the requests are executed from the same thread as we've called loadContributorsBlocking() from,
 * which is the main UI thread. The main thread gets blocked, and that explains why the UI is frozen.
 */
fun loadContributorsBlocking(service: GitHubService, req: RequestData) : List<User> {
    val repos = service
        .getOrgReposCall(req.org)
        .execute() // Executes request and blocks the current thread
        .also { logRepos(req, it) }
        .body() ?: listOf()

    return repos.flatMap { repo ->
        service
            .getRepoContributorsCall(req.org, repo.name)
            .execute() // Executes request and blocks the current thread
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}

fun <T> Response<List<T>>.bodyList(): List<T> {
    return body() ?: listOf()
}