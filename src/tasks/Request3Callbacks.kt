package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/*
fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        val allUsers = mutableListOf<User>()
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                logUsers(repo, responseUsers)
                val users = responseUsers.bodyList()
                allUsers += users
            }
        }
        // TODO: Why this code doesn't work? How to fix that?
        /*
         * We're starting many requests concurrently which lets us decrease the total loading time.
         * However, we don't wait for the loaded result.
         * We call the updateResults callback right after we started all the loading requests,
         * currently the allUsers list is not yet filled with the data.
         */
        updateResults(allUsers.aggregate())
    }
}
*/

/**
 * CountDownLatch stores a counter which we initialize with the number of repositories.
 * We decrement this counter after procecssing each repository and then wait until the latch has counted down to 0 before updating the results
 */
fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        val allUsers = mutableListOf<User>()
        val countDownLatch = CountDownLatch(repos.size)
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                logUsers(repo, responseUsers)
                val users = responseUsers.bodyList()
                allUsers += users
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        updateResults(allUsers.aggregate())
    }
}

inline fun <T> Call<T>.onResponse(crossinline callback: (Response<T>) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log.error("Call failed", t)
        }
    })
}
