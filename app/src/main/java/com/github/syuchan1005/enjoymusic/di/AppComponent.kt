package com.github.syuchan1005.enjoymusic.di

import com.github.syuchan1005.enjoymusic.App
import com.github.syuchan1005.enjoymusic.di.modules.MainModule
import com.github.syuchan1005.enjoymusic.di.modules.UiModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, MainModule::class, UiModule::class])
interface AppComponent : AndroidInjector<App> {

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: App): Builder

    fun build(): AppComponent
  }

  override fun inject(app: App)
}
