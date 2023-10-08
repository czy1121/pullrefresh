# PullRefreshLayout

实现了嵌套滚动机制的下拉刷新控件。  

- 支持下拉刷新
- 支持下拉进入二楼
- 支持嵌套滚动，实现了 NestedScrollingParent3, NestedScrollingChild3
  - 支持 NestedScrollView, RecyclerView 作为子视图 
  - 可用作 CoordinatorLayout 的嵌套滚动子视图(ScrollingViewBehavior)
- 支持 CoordinatorLayout 作为子视图，通过触摸事件实现
  - 支持 ScrollView/ListView/WebView 作为子视图


### PullRefreshLayout 嵌套 CoordinatorLayout 的下拉刷新不够丝滑？
 
CoordinatorLayout 未实现 NestedScrollingChild，所以不能通过嵌套滚动机制实现下拉刷新。 

那自定义一个 NestedCoordinatorLayout 实现 NestedScrollingChild3 不通过触摸事件能否实现下拉刷新呢？

答案是不能，以下面布局为例，在 NestedScrollView 或 RecyclerView 开始拖动，都很丝滑。

但无法从 AppBarLayout 开始拖下刷新头， 因为 AppBarLayout 未实现 NestedScrollingChild，不会产生嵌套滚动事件。
 

```
PullRefreshLayout
  CoordinatorLayout
    AppBarLayout
      View - ScrollHeader
      View - StickyHeader
    NestedScrollView 
      View
      RecyclerView
      View 
```


 

 


## Gradle

``` groovy
repositories {
    maven { url "https://gitee.com/ezy/repo/raw/cosmo/"}
}
dependencies {
    implementation "me.reezy.cosmo:pullrefresh:0.8.0"
}
```
 


## LICENSE

The Component is open-sourced software licensed under the [Apache license](LICENSE).