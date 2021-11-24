package tasks

import contributors.GitHubService
import contributors.RequestData
import contributors.User
import kotlin.concurrent.thread

/**
 * Fix the loadContributorsBackground() so that the resulting list was shown in the UI.
 *
 * Hint: callback is missing
 */
/*
fun loadContributorsBackground(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    thread {
        loadContributorsBlocking(service, req)
    }
}
*/
fun loadContributorsBackground(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    thread {
        updateResults(loadContributorsBlocking(service, req))
    }
}