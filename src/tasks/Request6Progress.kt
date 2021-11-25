package tasks

import contributors.*

/**
 * Store the intermediate list of loaded contributors in an aggregated state.
 * Define an variable which stores the list of users, and then update it after contributors for each new repos are loaded.
 */
/*
suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    TODO()
}
*/

/**
 * An updateResults callback is called after each request is completed.
 * We haven't used any concurrency so far.
 * This code is sequential, so we don't need synchronization.
 */
suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service.getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    var allUsers = emptyList<User>()
    for((index, repo) in repos.withIndex()) {
        val users = service.getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()

        allUsers = (allUsers + users).aggregate()
        updateResults(allUsers, index == repos.lastIndex)
    }
}