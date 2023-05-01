package com.jainhardik120.jiitcompanion.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.jainhardik120.jiitcompanion.R

@Composable
fun BannerAdView(isTest:Boolean = true) {
    val unitId = if (isTest) stringResource(id = R.string.banner_ad_test_id) else stringResource(
        id = R.string.banner_ad_id
    )
    AndroidView(factory ={
        context ->
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = unitId
            loadAd(AdRequest.Builder().build())
        }
    })

}