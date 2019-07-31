package com.movieous.media.mvp.model

import android.util.Log
import com.movieous.media.R
import com.movieous.media.mvp.model.entity.VideoListItem
import com.movieous.media.utils.SSLSocketFactoryCompat
import com.movieous.media.utils.Utils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.KeyStore
import java.util.*
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object VideoDataUtil {

    private val VIDEO_SOURCE = arrayOf("kuai-shou", "mei-pai", "huo-shan", "dou-yin")
    private val TIME_RANGE = arrayOf("week", "month")
    private val MAX_PAGE = 5
    private var SOURCE_INDEX: Int = 0

    private val mVideoList = ArrayList<VideoListItem>()

    val videoList: ArrayList<VideoListItem>
        get() {
            if (mVideoList.size == 0) {
                return createData()
            }
            if (SOURCE_INDEX < 3) {
                doGetVideoList()
            }
            return mVideoList
        }

    private val trustManager: X509TrustManager?
        get() {
            try {
                val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                tmf.init(null as KeyStore?)
                for (tm in tmf.trustManagers) {
                    if (tm is X509TrustManager) {
                        return tm
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

    fun doGetVideoList() {
        Thread { getVideoListInternal() }.start()
    }

    // 仅做测试之用
    private fun getVideoListInternal() {
        val rangeIndex = 1//Utils.getRandomNum(0, 1)
        val url = String.format(
            "http://kuaiyinshi.com/api/hot/videos/?source=%s&page=%d&st=%s&_=%d",
            VIDEO_SOURCE[SOURCE_INDEX], MAX_PAGE, TIME_RANGE[rangeIndex], System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        )
        SOURCE_INDEX++
        val okHttpClient = OkHttpClient.Builder().sslSocketFactory(SSLSocketFactoryCompat(), trustManager!!).build()
        val request = Request.Builder().url(url).build()
        try {
            val response = okHttpClient.newCall(request).execute()
            parseResponse(response)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun parseResponse(response: Response) {
        if (!response.isSuccessful) {
            return
        }
        try {
            val json = JSONObject(response.body()!!.string())
            if (json.optInt("code") != 200) {
                Log.e("movieous", "code = " + json.optInt("code"))
                return
            }
            val data = json.optJSONArray("data")
            //mVideoList.clear();
            for (i in 0 until data.length()) {
                val videoItem = data.getJSONObject(i)
                val head = "http:"
                val videoUrl = head + videoItem.optString("video_url")
                val userName = videoItem.optString("nickname")
                val content = videoItem.optString("desc")
                val coverUrl = head + videoItem.optString("video_img")
                val avatarUrl = head + videoItem.optString("avatar", null)
                val videoBean = if (avatarUrl == null)
                    VideoListItem(R.drawable.avatar1, videoUrl, userName, content, coverUrl, 720, 1280)
                else
                    VideoListItem(videoUrl, userName, content, coverUrl, avatarUrl, 720, 1280)
                mVideoList.add(videoBean)
            }
            Log.i("movieous", "get video list length = " + data.length())
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun createData(): ArrayList<VideoListItem> {

        val videoItemBeanList = ArrayList<VideoListItem>()

        videoItemBeanList.add(
            VideoListItem(
                R.drawable.avatar1,
                "https://api.amemv.com/aweme/v1/playwm/?video_id=v0200f7c0000bgfl6h9cgf374t645hfg&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "茹cut",
                "#彭于晏 祝姐妹们的男朋友身材跟彭于晏一样棒\uD83D\uDD25",
                "https://p9-dy.bytecdn.cn/large/14f9d00014cbcc1785d3a.jpeg",
                720,
                1280
            )
        )
        videoItemBeanList.add(
            VideoListItem(
                R.drawable.avatar1,
                "https://api.amemv.com/aweme/v1/playwm/?video_id=v0200f720000bgfo5g2j2bov50kp8nug&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "May",
                "贫民窟女孩的艰辛",
                "https://p3-dy.bytecdn.cn/large/14ee30005a250f311540b.jpeg",
                720,
                1280
            )
        )
        videoItemBeanList.add(
            VideoListItem(
                R.drawable.avatar1,
                "https://api.amemv.com/aweme/v1/playwm/?video_id=v0200fca0000bgff4okeae1ciqlplo6g&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "俺有正能量",
                "#正能量 #励志 @抖音小助手 记住我一定会翻脸。",
                "https://p3-dy.bytecdn.cn/large/14eb60005368f55e2320f.jpeg",
                720,
                1280
            )
        )
        videoItemBeanList.add(
            VideoListItem(
                R.drawable.avatar1,
                "https://api.amemv.com/aweme/v1/playwm/?video_id=v0200f720000bgfdmtt34q18rg9asfm0&line=0&ratio=720p&media_type=4&vr_type=0&test_cdn=None&improve_bitrate=0",
                "请叫我田能能",
                "南门九爷，超级喜欢",
                "https://p9-dy.bytecdn.cn/large/14dbd00050704269301f9.jpeg",
                720,
                1280
            )
        )

        return videoItemBeanList
    }
}
