package com.threekidsinatrenchcoat.giftideaminder.di

import android.content.Context
import com.threekidsinatrenchcoat.giftideaminder.BuildConfig
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.Analytics
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.AnalyticsImpl
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.AnalyticsProvider
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.FirebaseAnalyticsProvider
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.NoOpAnalyticsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    /**
     * Provides the main Analytics interface implementation
     */
    @Provides
    @Singleton
    fun provideAnalytics(
        providers: Set<@JvmSuppressWildcards AnalyticsProvider>
    ): Analytics {
        return AnalyticsImpl(
            providers = providers,
            debugEnabled = BuildConfig.DEBUG
        )
    }

    /**
     * Provides NoOpAnalyticsProvider for development/testing
     * Always included to ensure the app works without any external analytics services
     */
    @Provides
    @IntoSet
    fun provideNoOpAnalyticsProvider(): AnalyticsProvider {
        return NoOpAnalyticsProvider()
    }

    /**
     * Provides FirebaseAnalyticsProvider for production analytics
     * Can be conditionally enabled based on build config or feature flags
     */
    @Provides
    @IntoSet
    fun provideFirebaseAnalyticsProvider(
        @ApplicationContext context: Context
    ): AnalyticsProvider {
        val provider = FirebaseAnalyticsProvider()
        
        // Initialize the provider if not in debug mode
        // In debug mode, we rely on NoOpAnalyticsProvider
        if (!BuildConfig.DEBUG) {
            provider.initialize(context)
        }
        
        return provider
    }
}