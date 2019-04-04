package com.xiang.sample.diycamera.bean

//data class LookupFilterBean(var list: ArrayList<ContentBean>)

//data class ContentBean(var index: Int, var name: String)

class LookupFilterBean {
    private var content = ArrayList<ContentBean>()

    fun setContent(content: ArrayList<ContentBean>) {
        this.content = content
    }

    fun getContent(): ArrayList<ContentBean> {
        return this.content
    }
}

class ContentBean {
    private var index = -1
    private var name = ""

    fun setIndex(index: Int) {
        this.index = index
    }

    fun getIndex(): Int {
        return this.index
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return this.name
    }
}