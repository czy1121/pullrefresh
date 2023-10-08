package me.reezy.cosmo.pullrefresh;

enum class PullState {

    None,       // 初始状态，
    Pulling,    // 拖动状态(释放回到初始状态)
    Ready,      // 拖动状态(释放开始加载)
    Loading,    // 加载状态(已经释放)
    Loaded,     // 完成状态(加载成功)，悬停1秒回到初始状态
    Failed,     // 完成状态(加载失败)，悬停1秒回到初始状态

    SecondReady,    // 拖动状态(释放打开二楼)
    SecondFloor,    // 二楼打开状态
    ;
}