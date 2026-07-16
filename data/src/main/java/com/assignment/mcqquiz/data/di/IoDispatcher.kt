package com.assignment.mcqquiz.data.di

import javax.inject.Qualifier

/**
 * Qualifier for the IO [kotlinx.coroutines.CoroutineDispatcher].
 * Bound to [kotlinx.coroutines.Dispatchers.IO] in production;
 * replace with a test dispatcher (e.g. UnconfinedTestDispatcher) in unit tests.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher


