/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xd.testrecorder.python

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.xd.common.coroutine.io
import com.xd.common.logger.Logger
import com.xd.testrecorder.dao.RecordingDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class PythonViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao
) : ViewModel() {
    private val logger = Logger("PythonViewModel")
    val result = MutableLiveData<String>()

    init {
        logger.d("PythonViewModel.init")
    }

    fun runCode(code: String) {
        io {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(context))
            }
            val python = Python.getInstance()
            val pythonScript = """
count = 1
def android_bridge(params):
    # key1 = params["key1"]
    # key2 = params["key2"]
    # # print(f"key1:{key1}")
    print(params.get('key2'))
    global count
    count += 1
    print(count)
"""

            val returnOutput: MutableMap<String, Any?> = HashMap()
            var console: PyObject = python.getModule("bridge")
            val sys: PyObject = python.getModule("sys")
            val io: PyObject = python.getModule("io")

            try {
                val textOutputStream: PyObject = io.callAttr("StringIO")
                sys["stdout"] = textOutputStream
                console.callAttrThrows("run_code", pythonScript)

                val map = HashMap<String, Any>()
                map["key1"] = 1
                map["key2"] = "b"
                console.callAttrThrows("call_android_bridge", map)
                console.callAttrThrows("call_android_bridge", map)
                returnOutput["textOutputOrError"] =
                    textOutputStream.callAttr("getvalue").toString()
            } catch (e: PyException) {
                logger.e("PyException $e")
                returnOutput["textOutputOrError"] = e.message.toString()
            }
            result.postValue(returnOutput.toString())
        }
    }

}

