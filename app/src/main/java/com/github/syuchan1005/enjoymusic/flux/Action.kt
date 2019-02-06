package com.github.syuchan1005.enjoymusic.flux

interface Action<out T> {
  val type: String
  val data: T
}
