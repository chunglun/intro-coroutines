package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

/**
 * Implement TODO(), use the previous loadContributorsSuspend()
 *
 * 1. copy the implementation of loadContributorsConcurrent() to loadContributorsNotCancellable()
 * 2. remove the creation of a new coroutineScope
 */
/*
suspend fun loadContributorsNotCancellable(service: GitHubService, req: RequestData): List<User> {
    TODO()
}
*/

suspend fun loadContributorsNotCancellable(service: GitHubService, req: RequestData): List<User> {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    val result = repos.map { repo ->
        GlobalScope.async {
            log("starting loading for ${repo.name}")
            delay(3000)
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }.awaitAll().flatten().aggregate()

    return result
}