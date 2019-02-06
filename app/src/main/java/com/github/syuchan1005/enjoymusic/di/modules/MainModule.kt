package com.github.syuchan1005.enjoymusic.di.modules

import dagger.Module
import dagger.Provides
import com.github.syuchan1005.enjoymusic.di.PerActivityScope
import com.github.syuchan1005.enjoymusic.flux.Dispatcher
import com.github.syuchan1005.enjoymusic.store.MainStore

@Module
internal class MainModule {
  @PerActivityScope
  @Provides
  fun provideMainStore(dispatcher: Dispatcher) = MainStore(dispatcher)
}
