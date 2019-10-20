package com.example.feature01

import com.example.dynamicfeaturetutorial.MainActivity
import dagger.Module

/**
 * Created by Franz Andel on 2019-08-28.
 * Android Engineer
 */

@Module
class featureClass {

    fun featureFunction() {
        MainActivity::class.simpleName
    }
}