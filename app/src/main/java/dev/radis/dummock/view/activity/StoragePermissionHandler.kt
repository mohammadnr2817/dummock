package dev.radis.dummock.view.activity

interface StoragePermissionHandler {

    fun requestPermission(callback: (Boolean) -> Unit = {})

}