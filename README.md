[![official JetBrains project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

# Introduction to Coroutines and Channels Hands-On Lab

This repository is the code corresponding to the
[Introduction to Coroutines and Channels](https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/01_Introduction)
Hands-On Lab. 

[Tutorial](https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/04_Suspend)

> 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

### Tasks
-[ ] (Task1) open src/tasks/Aggregation.kt and implement List<User>.aggregate()

### 1. Blocking Request
![img.png](img.png)

### 2. Callbacks
The previous solution works, but blocks the thread and therefore freezes the UI. 
A traditional approach to avoiding this is th use callbacks.

2 ways to make the UI responsive:
* Move the whole computation to a separate thread T1
![img_1.png](img_1.png)
* Switch to Retrofit API and start using callbacks instead if blocking calls

Handling the data for each repository should be then divided into 2 parts: first loading, then processing the resulting response. 
The second "processing" part should be extracted into a callback.
The loading for each repository can then be started before the result for the previous repository is received.
![img_2.png](img_2.png)

### 3. Suspend Functions
![img_3.png](img_3.png)