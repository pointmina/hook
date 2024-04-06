package com.hanto.Hook

class FolderInfo {
    var folderName: String? = null
        set(value) {
            field = value
            if (value == null) {
                folderColor = null
            } else {
                folderColor = "#D9D9D9"
            }
        }
    var folderColor: String? = null
}
