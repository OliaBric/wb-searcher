package com.oliabric.wbsearcher

import com.oliabric.wbsearcher.data.CatalogRequest
import com.oliabric.wbsearcher.data.Res
import com.google.gson.Gson
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.bind.annotation.*
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.sqrt


@RestController
class HelloController {
    @GetMapping("/")
    fun index(): String {

        val image = "https://images.wbstatic.net/c246x328/new/4210000/4210319-1.jpg"

        return "<H1>Greetings from Spring Boot</H1><br>" +
                "<img src=\"" + image + "\" />"
    }

    @RequestMapping(value = ["/image"], method = [RequestMethod.GET], produces = ["image/jpg"])
    @ResponseBody
    fun getFile(url: String): ByteArray? {
        return try {
            val `is`: InputStream = javaClass.getResourceAsStream("/com/baeldung/produceimage/image.jpg")
//            val `is` = this.javaClass.getResourceAsStream(url)
            val img = ImageIO.read(`is`)
            val bao = ByteArrayOutputStream()
            ImageIO.write(img, "jpg", bao)
            bao.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @PostMapping("/url")
    fun getUrl(url: String, color: String): String? {

        val imageUrl = "https://images.wbstatic.net/c246x328/new"

        val restTemplate = RestTemplateBuilder()
        val restService = RestService(restTemplate)

        val res = restService.getPostsPlainJSON(url)

        val catalogRequest = Gson().fromJson(res, CatalogRequest::class.java)

        val resList = mutableListOf<Res>()
        if (res != null)
            for (product in catalogRequest.data.products) {
                val sbUrl: StringBuilder = StringBuilder()
                sbUrl.append(imageUrl)
                sbUrl.append("/")
                sbUrl.append(product.id - product.id % 10000)
                sbUrl.append("/")
                sbUrl.append(product.id)
                sbUrl.append("-1.jpg")

                val imageUrl = URL(sbUrl.toString())
                val image: BufferedImage = ImageIO.read(imageUrl)

                val c = getAverageColor(image)

                resList.add(Res(product, sbUrl.toString(), c, colorDistance(c, Color.decode(color).rgb)))
            }

        Collections.sort(resList, distanceComparator)

        val imgs = StringBuilder()
        for(res in resList){

            val hexColor = String.format("#%06X", 0xFFFFFF and res.color)

            val s = "<img src=\"" + res.imageUrl + "\" />" +
                    "<svg width=\"50\" height=\"50\">\n" +
                    "<rect width=\"50\" height=\"50\" style=\"fill:" + hexColor + "\" />\n" +
                    "</svg>" +
                    "<br>"

            imgs.append(s)

        }

        return imgs.toString()
    }

    fun getAverageColor(image: BufferedImage): Int {
        var redBucket = 0
        var greenBucket = 0
        var blueBucket = 0
        var pixelCount = 0

        val halfY = image.height / 2
        val halfYStart = halfY - halfY / 10
        val halfYEnd = halfY + halfY / 10
        val halfX = image.width / 2
        val halfXStart = halfX - halfX / 10
        val halfXEnd = halfX + halfX / 10

        for (x in halfXStart until halfXEnd) {
            for (y in halfYStart until halfYEnd) {
                val color: Int = image.getRGB(x, y)

                val red = Color(color).red
                val green = Color(color).green
                val blue = Color(color).blue

                if (red == 255 && green == 255 && blue == 255) {
                    // is white
                } else {
                    redBucket += red
                    greenBucket += green
                    blueBucket += blue
                }
                pixelCount++
            }
        }

        return Color(
            redBucket / pixelCount,
            greenBucket / pixelCount,
            blueBucket / pixelCount
        ).rgb
    }

    fun colorDistance(color1: Int, color2: Int): Double {
        val r1 = Color(color1).red
        val g1 = Color(color1).green
        val b1 = Color(color1).blue

        val r2 = Color(color2).red
        val g2 = Color(color2).green
        val b2 = Color(color2).blue

        val rmean = (r1 + r2) / 2
        val r = r1 - r2
        val g = g1 - g2
        val b = b1 - b2
        return sqrt(((512 + rmean) * r * r shr 8).toDouble() + 4 * g * g + ((767 - rmean) * b * b shr 8).toDouble())
    }

    private val distanceComparator =
        Comparator { o1: Res, o2: Res ->
            o1.distance.toInt().compareTo(o2.distance.toInt())
        }
}