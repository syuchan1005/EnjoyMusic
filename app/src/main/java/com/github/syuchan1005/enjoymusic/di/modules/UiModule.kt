package com.github.syuchan1005.enjoymusic.di.modules

import com.github.syuchan1005.enjoymusic.di.PerActivityScope
import com.github.syuchan1005.enjoymusic.view.AddActivity
import com.github.syuchan1005.enjoymusic.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class UiModule {

  @PerActivityScope
  @ContributesAndroidInjector(modules = [MainModule::class])
  internal abstract fun contributeMainActivity(): MainActivity

  @PerActivityScope
  @ContributesAndroidInjector(modules = [MainModule::class])
  internal abstract fun contributeAddActivity(): AddActivity
}
