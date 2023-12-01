package com.xd.mvvm.boilerplate.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Recording(
    @Id
    var id: Long = 0,
    var name: String? = "Unnamed Recording",
    var createTime: Long = System.currentTimeMillis(), // Set createTime when the object is created
    var updateTime: Long = createTime
)