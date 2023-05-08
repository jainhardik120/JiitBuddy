package com.jainhardik120.jiitcompanion.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.jainhardik120.jiitcompanion.R

private const val TAG = "NativeAdView"

@Composable
fun NativeAdView(isTest: Boolean = true) {
    val unitId = if (isTest) stringResource(id = R.string.native_ad_test_id) else stringResource(
        id = R.string.native_ad_id
    )
    val context = LocalContext.current
    val adLoader = AdLoader.Builder(context, unitId)
        .forNativeAd {
            Log.d(TAG, "NativeAdView: ${it.body}")
            Log.d(TAG, "NativeAdView: ${it.advertiser}")
            Log.d(TAG, "NativeAdView: ${it.headline}")
            Log.d(TAG, "NativeAdView: ${it.price}")
        }
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad: ${adError.message}")
            }
        })
        .withNativeAdOptions(
            NativeAdOptions.Builder()
            .build())
        .build()
    adLoader.loadAd(AdRequest.Builder().build())
}