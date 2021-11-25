package tasks

import contributors.*
import kotlinx.coroutines.*

/**
 * Implement TODO(), use the previous loadContributorsSuspend()
 *
 * Tip: wrap each contributors request with async --> will create as many coroutines as number of repos we have.
 * async returns Deferred<List<User>>
 * awaitAll() returns List<List<User>>
 */
/*
suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    TODO()
}
*/

/**
 * Run the code and check the log --> all the coroutines still run on the main UI thread.
 * Haven't yet employed multithreading but already have the benefits of running coroutines concurrently.
 */
/*
suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        async {
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }

    deferreds.awaitAll().flatten().aggregate()
}
 */

/**
 * To run contributors coroutines on different threads from the common thread pool
 * --> Specify Dispatchers.Default as the context argument for the async function
 *
 * CoroutineDispatcher determintes what thread or threads the corresponding coroutine should be run on.
 * If we don't specify one as an argument, then async will use the dispatcher from the outer scope.
 *
 * Dispatchers.Default represents a shared pool of threads on JVM.
 * This pool provides a means for parallel execution.
 * It consists of as many threads as there are CPU cores available, but still it has two threads if there's only one core.
 */
suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        async(Dispatchers.Default) {
            log("starting loading for ${repo.name}")
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }

    deferreds.awaitAll().flatten().aggregate()
}