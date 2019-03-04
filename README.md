# DxUtils-lib
convenience methods that i use a lot

# Usage:
in your **root** build.gradle:

    allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}

or in your **module's** build.gradle file:

	repositories {
	    ...
	    maven { url 'https://jitpack.io' }
	}


in your **module's** build.gradle also add:

  	dependencies {
	        implementation 'com.github.or-dvir.DxUtils-lib:dxutils:{latest version}'        
	}

