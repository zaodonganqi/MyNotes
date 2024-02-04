package com.bean

class RemindBean {
    private var id: Int = -1
    private var title: String = ""
    private var image: String = ""
    private var mp3: String = ""

    fun getId(): Int {
        return this.id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getTitle(): String {
        return this.title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getImage(): String {
        return this.image
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getmp3(): String {
        return this.mp3
    }
    fun setmp3(mp3: String) {
        this.mp3 = mp3
    }

}