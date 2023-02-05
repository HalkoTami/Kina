




<img align ="left" width = "150"  src="https://user-images.githubusercontent.com/101169544/216797263-943b166c-7bfc-48ef-8d25-2a9d21c4f592.png"/> 
<h1 align="left">Kina</h1>
Kina is an android app where you can create, edit, memorize flashcards all in once!
<h1 align="left">  </h1>
<div align="right">

[<img src="https://user-images.githubusercontent.com/101169544/216798077-21143ef2-5281-4da0-8d13-ece63c9bdf8c.png" height="100"/>](https://play.google.com/store/apps/details?id=com.koronnu.kina)

</div>


 
[](https://play.google.com/store/apps/details?id=com.koronnu.kina)

<div align="center">
<img src="https://user-images.githubusercontent.com/101169544/216797270-c6657b6f-f92d-4620-9208-512fe3c50011.gif" width ="200">　<img src="https://user-images.githubusercontent.com/101169544/215409698-1a7a02e4-65fb-4bc3-b170-18b198c8bf21.gif" width ="200">　<img src="https://user-images.githubusercontent.com/101169544/216797286-2661c16f-0b92-46e7-a1a6-db15d79feae1.gif" width ="200">
</div>


# Download

Download latest version of Kina [here](https://github.com/HalkoTami/KiNa/releases)

## Welcome to my Kina Repository

Kina is developed by me, Halko Tami since March 2022, and is publicised as my portfolio for job searching as Android Developer.

When I started writing codes for Kina, I basically had 0 knowledge about programming, or anything a developer has to know to release an app. Recently, I came so far to make this app somewhat usable, and release it in Google Play. 

I hope you can make a rough estimate of what I am (or am not) capable of, by browsing this repository!

# About this App

## Tech stack

- language: Kotlin
- Minimum SDK Level 21
- Jetpack Library
    - Fragment: Kina is basically a single activity that consists of multiple `fragments`. Fragment binds the view and provides `LifecycleOwner` to observe `LiveData` from `ViewModel`.
    
    - Lifecycle: Most of the fragments has its own `ViewModel`. ViewModel contains data that the Fragment’s shown View consists of. Some data may be variables such as `visibility`, others  are `flows` created from local database. ViewModels which need access to local data have data layer, in form of repository, as parameter in their constructor. `ViewModel Factory` which is defiened in each ViewModel, and it will be used to retrieve an instance of that ViewModel.
    
    - Navigation: To navigate between the fragments, Kina uses `NavController`. Destinations are determined by navigation graph inflated from resource file. The arguments of each destinations are also defined there.
    
    - Room: Kina refers to local database. Using `Room` not only enabled Kina to reduce long detailed SQLite queries, but to verify queries at compile time. Database class defines multiple Entity classes, that will have a mapping SQLite table. Database class also  contains `Dao(Data Access Object)` class for each Entity. Using Dao, you interact with database, return table data as flow.
    A single repository declares all Daos as private property in the constructor, and manage database without whole database being exposed in it. This repository is called in several ViewModels, supplies required data every time when the result updates.
        
        I followed the steps of this [codelab](https://developer.android.com/codelabs/android-room-with-a-view-kotlin#0)  
        
    - Recyclerview: Recyclerview is used with different types of `ListAdapter`, and display large sets of database contents, which in Kina’s case are their flashcards, and including cards.
    
    - DataBinding: To reduce view UI framework calls in Fragment such as `setClicklistener()` or `setText()`, Kina enabled `DataBinding` along the way of developing. Variables will be set in inflated view binding class. They are most commonly inflating fragment’s lifecycleowner and related ViewModel. Views in the layout observes data changes without setting any observe methods in activity. The outcome of this attempt seems pretty effective. I have less codes and it feels much easier to maintain them. Migration has not completed yet, therefore you’ll see some layouts that are still in progress.

## Kina’s Issues

Since I am a beginner, this app is built on a very poor understanding. It has a lot of potential errors and problems, and contains bad code writing. 

However, this means there is room for improvement, so I will keep working on it!

Here are some [Issues](https://github.com/HalkoTami/Kina/issues) I am aware of right now.

## Author

Halko Tami, programming beginner, trying to get a job as Android App Developer.

[twitter](https://twitter.com/halkoAusD), [qiita](https://qiita.com/halkoAusD)

know more about me and stuffs I created: my portfolio

## License

```markdown
Copyright 2022 Halko Tami

Licensed under the Apache License, Version 2.0 (the “License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

See the License for the specific language governing permissions and
limitations under the License.
```
