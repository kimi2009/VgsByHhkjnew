package com.hhkj.vgsbyhhkjnew

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.io.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis


val hztxt = HashMap<Char, Pair<Int, FloatArray>>()
var bls = HashMap<String, dwent>()
var rlst = ArrayList<dwent>()
var disrect =
    doubleArrayOf(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE)
var rlstready = false
var isLoading = false
var fontInited = false
//data class cadpackfile(var dr: DoubleArray, var bs: HashMap<String, dwent>, var rs: ArrayList<dwent>)

open class skiaView(val ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {
    private var mScaleGestureDetector: ScaleGestureDetector =
        ScaleGestureDetector(context, ScaleGestureListener())
    private var mCadViewHandler: CadViewListener? = null
//    val hztxt = HashMap<Char, Pair<Int, FloatArray>>()
//    var bls = HashMap<String, dwent>()
//    var rlst = ArrayList<dwent>()
//    var disrect = doubleArrayOf(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE)

    //public data class mtext(val etype: String, val info: String)

    //    lateinit var wmgr: WifiManager
    val mpaint = Paint()
    val gpaint = Paint()
    val rpaint = Paint()
    val textPaint = TextPaint()

    //    lateinit var rlst: ArrayList<dwent>     //mblock
//    var bls = HashMap<String, dwent>()        //blocks
    val sels = HashSet<Long>()
    private val faultLabelInfos = mutableListOf<FaultLabelInfo>()

    init {
        mpaint.textSize = 1f
        mpaint.style = Paint.Style.STROKE
        mpaint.typeface = Typeface.SERIF
        gpaint.color = Color.GREEN
        rpaint.color = Color.RED
        rpaint.style = Paint.Style.STROKE
        textPaint.textSize = 1f
        textPaint.style = Paint.Style.STROKE
        textPaint.typeface = Typeface.SERIF
        textPaint.color = Color.WHITE
    }

    fun getFaultLabelInfos(): MutableList<FaultLabelInfo> {
        return faultLabelInfos
    }

    fun setCadViewListener(listener: CadViewListener) {
        mCadViewHandler = listener
    }

    //var cadInterface: CadInterface? = null

    // 提供注册事件监听的方法
    /* fun setCadInterfaceListener(cadInterface: CadInterface) {
         this.cadInterface = cadInterface
     }*/

    lateinit var gbmp: Bitmap
    var cscale = 1.0f
    var mdx = 0f
    var mdy = 0f
    var mvx = 0f
    var mvy = 0f
    fun dooffset(ev: MotionEvent): Boolean {
        //解决两指没有同时离开的问题
        if ((System.currentTimeMillis() - ScaleGestureTime) < 500) {
            return true
        }
        if (ev.action == MotionEvent.ACTION_MOVE) {
            /*if (ev.eventTime - ev.downTime < 500){
                return true
            }*/
//            val hs = ev.historySize
//            if (hs > 0) {
//                scrollBy((ev.getHistoricalX(hs - 1) - ev.x).toInt() * 5, (ev.getHistoricalY(hs - 1) - ev.y).toInt() * 5)
//            } else {
            if (lineselect) { //线框选模式
                mvx = ev.x
                mvy = ev.y
                invalidate()
            } else {
                if (abs(mdx - ev.x) > 1) {
                    val sbx =
                        max(abs((mdx - ev.x) / cscale), 1f).toInt() * (if (mdx > ev.x) 1 else -1)
                    val sby =
                        max(abs((mdy - ev.y) / cscale), 1f).toInt() * (if (mdy > ev.y) 1 else -1)
                    scrollX += sbx
                    scrollY += sby
                    mdx = ev.x
                    mdy = ev.y
                    //println("====in dooffset invalidate.scrollX:${scrollX}+++scrollY:${scrollY}++mdx:${mdx}++mdy:${mdy}")
//                    invalidate()
//                    println("in dooffset invalidate.")
//                }
                }
            }
        } else if (ev.action == MotionEvent.ACTION_UP) {
            if (ev.eventTime - ev.downTime < 300 && (!lineselect)) {  //单点了一下
                println("===原始ev.x: ${ev.x}, ev.y: ${ev.y},cscale:${cscale}")
                println("===放缩x: ${ev.x / cscale}, y: ${ev.y / cscale}")
                println("===算后x: ${ev.x / cscale + vr.left}, y: ${ev.y / cscale + vr.top}")
                val handle = gethandle(ev.x / cscale + vr.left, ev.y / cscale + vr.top)
                val cav = Canvas()
                cav.drawCircle(ev.x, ev.y, 2f, rpaint)
                //       4097.225360159429===-1674.05334102597===4885.7551326701205===-1664.05334102597
                if (handle == 0L) {
                    sels.clear()
                    mCadViewHandler?.onClearSelectObject()
                } else {
                    // println("====收到点击，开始查找")
                    mCadViewHandler?.apply {
                        if (isObjectSelectable(handle))
                            onSelectObject(handle)
                        else {
                            sels.clear()
                            mCadViewHandler?.onClearSelectObject()
                        }
                    }
                    // println("====收到点击，查找完成")
                }

//                Thread{
//                    if(gbmp != null){
//                        val cav = Canvas(gbmp)
////                        mdraw(cav)
//                        cav.drawColor(Color.RED)
//                        val os = FileOutputStream(File("/sdcard/Download/skiaviewdata/t.png"))
//                        gbmp.compress(Bitmap.CompressFormat.PNG, 100, os)
//                        os.flush()
//                        os.close()
//                    }
//                    val fbmp = Bitmap.createBitmap((disrect[2]-disrect[0]).toInt()/5, (disrect[3]-disrect[1]).toInt()/5, Bitmap.Config.RGB_565)
//                    val cav = Canvas(fbmp)
//                    cav.drawColor(Color.WHITE)
//                    cav.translate(-disrect[0].toFloat(), -disrect[1].toFloat())
//                    cav.scale(0.2f, 0.2f, disrect[0].toFloat(), disrect[1].toFloat())
//                    mdraw(cav)
                //cav.drawRect(100f, 100f, 1000f, 1000f, gpaint)
//                    val os = FileOutputStream(File("/sdcard/Download/skiaviewdata/t.png"))
//                    fbmp.compress(Bitmap.CompressFormat.PNG, 100, os)
//                    os.flush()
//                    os.close()
//                }//.start()
            }
            if (ev.pointerCount == 1 && lineselect) { //线选模式下，当鼠标抬起时，计算选定线集
                gethandle(
                    mdx / cscale + vr.left,
                    mdy / cscale + vr.top,
                    mvx / cscale + vr.left,
                    mvy / cscale + vr.top
                )
                if (sels.size > 0)
                    mCadViewHandler?.onSelectObjects(sels)

            }

            selectMode = false

            mdx = 0f
            mdy = 0f
            invalidate()
        }
        return true
    }

    fun gethandle(x: Float, y: Float): Long {
        rlst.forEach {
            var b = it.disrect[0] < x && it.disrect[2] > x && it.disrect[1] < y && it.disrect[3] > y

            /*println("===it.disrect[0] < x:"+(it.disrect[0] < x))
            println("===it.disrect[2] > x:"+(it.disrect[2] > x))
            println("===it.disrect[1] < y:"+(it.disrect[1] < y))
            println("===it.disrect[3] > y:"+(it.disrect[3] > y))
            println("===是否入袋:"+b)*/
            if (b) {
                if (bisselected(x, y, it)) {
                    //ctx.toast("Handle: ${it.handle} info: ${it.info}")
                    //画范围矩形
                    val cav = Canvas()
                    println("矩形" + it.disrect[0] + "===" + it.disrect[1] + "===" + it.disrect[2] + "===" + it.disrect[3])
                    cav.drawRect(
                        it.disrect[0].toFloat(),
                        it.disrect[1].toFloat(),
                        it.disrect[2].toFloat(),
                        it.disrect[3].toFloat(),
                        gpaint
                    )

                    sels.clear()
                    sels.add(it.handle)
                    //centerviewdwent(it)
                    return it.handle
                }
            }
        }
        return 0;
    }

    fun gethandle(ltx: Float, lty: Float, rbx: Float, rby: Float): Long {
        val sq = arrayOf(min(ltx, rbx), min(lty, rby), max(ltx, rbx), max(lty, rby)).toFloatArray()
        sels.clear()
        rlst.forEach {
            if (it.etype == 17 || it.etype == 18) {   //框选时，只能选中线段。
                if (RectF(
                        it.disrect[0].toFloat(), it.disrect[1].toFloat(), it.disrect[2].toFloat(),
                        it.disrect[3].toFloat()
                    ).intersect(sq[0], sq[1], sq[2], sq[3])
                ) {
                    for (i in 0 until it.lwpolyline_fpts.size step 4) {
                        val (x1, y1, x2, y2) = it.lwpolyline_fpts.slice(i until i + 4)
                        //计算两条线是否有交点
                        if (islineinrect(
                                arrayOf(x1, y1).toFloatArray(),
                                arrayOf(x2, y2).toFloatArray(),
                                sq
                            )
                        ) {
                            sels.add(it.handle)
                        }
                    }
                }
            }
        }
        return 0
    }

    fun cross(p1: FloatArray, p2: FloatArray, p3: FloatArray): Float {
        val x1 = p2[0] - p1[0]
        val y1 = p2[1] - p1[1]
        val x2 = p3[0] - p1[0]
        val y2 = p3[1] - p1[1]
        return x1 * y2 - x2 * y1
    }

    fun segment(p1: FloatArray, p2: FloatArray, p3: FloatArray, p4: FloatArray): Boolean {
        if (max(p1[0], p2[0]) >= min(p3[0], p4[0]) //矩形1最右端大于矩形2最左端
            && max(p3[0], p4[0]) >= min(p1[0], p2[0]) //矩形2最右端大于矩形1最左端
            && max(p1[1], p2[1]) >= min(p3[1], p4[1]) //矩形1最高端大于矩形2最低端
            && max(p3[1], p4[1]) >= min(p1[1], p2[1])  //矩形2最高端大于矩形1最低端
        ) {
            if (cross(p1, p2, p3) * cross(p1, p2, p4) <= 0
                && cross(p3, p4, p1) * cross(p3, p4, p2) <= 0
            )
                return true
        }
        return false
    }

    fun islineinrect(l1: FloatArray, l2: FloatArray, sq: FloatArray): Boolean {
        // step 1 check if end point is in the square
        if ((l1[0] >= sq[0] && l1[1] >= sq[1] && l1[0] <= sq[2] && l1[1] <= sq[3]) ||
            (l2[0] >= sq[0] && l2[1] >= sq[1] && l2[0] <= sq[2] && l2[1] <= sq[3])
        )
            return true
        else {
            // step 2 check if diagonal cross the segment
            val p1 = arrayOf(sq[0], sq[1]).toFloatArray()
            val p2 = arrayOf(sq[2], sq[3]).toFloatArray()
            val p3 = arrayOf(sq[2], sq[1]).toFloatArray()
            val p4 = arrayOf(sq[0], sq[3]).toFloatArray()
            if (segment(l1, l2, p1, p2) || segment(l1, l2, p3, p4))
                return true
        }
        return false
    }

    fun clearSelected() {
        sels.clear()
    }

    fun addSelected(objList: List<Long>) {
        sels.addAll(objList)
    }

    fun bisselected(x: Float, y: Float, it: dwent): Boolean {
        mCadViewHandler?.apply {
            if (!isObjectSelectable(it.handle))
                return false
        }
        when (it.etype) {
            17, 18 -> {
                var cds = Float.MAX_VALUE
                for (i in 0 until it.lwpolyline_fpts.size step 4) {
                    val (x1, y1, x2, y2) = it.lwpolyline_fpts.slice(i until i + 4)
                    //直线方程：Ax+By+c=0
                    val A = y2 - y1
                    val B = x1 - x2
                    val C = x2 * y1 - x1 * y2
                    //点到线距离公式
                    val ds = abs((A * x + B * y + C) / sqrt(A * A + B * B))
                    cds = min(cds, ds)
                }
                if (cds < 20) return true
                else return false
            }
        }
        return true
    }

    fun mscale(rsc: Float) {
        cscale = min(cscale * rsc, 40.0f)
        invalidate()
    }

    fun setScale(scale: Float) {
        cscale = scale
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        println("in onTouchEvent.")
        if (!lineselect) {
            mScaleGestureDetector.onTouchEvent(event)
            //return true
        }
        //return super.onTouchEvent(event)
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (lineselect)
                selectMode = true
            mdx = event.x
            mdy = event.y
            return true
        }

        when (event?.pointerCount) {
            1 -> return dooffset(event)
            3 -> {
                if (disrect[0] == Double.MAX_VALUE) {
                    ctx.toast("wait for cad data ready. byMaxvalue")
                    return true
                }

                showall()
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    var cenerview = false
    var minScale = 0.01f
    fun showall() {
        if (disrect[0] < Double.MAX_VALUE)
            cscale =
                abs(
                    min(
                        width / (disrect[2] - disrect[0]),
                        height / (disrect[3] - disrect[1])
                    ).toFloat()
                ) * 1.0f
        else {
            cscale = 1.0f
        }
        if (!cscale.isFinite()) cscale = 1.0f
        if (cscale > 1) cscale = 1.0f
        scrollTo(0, 0)
        cenerview = true
        minScale = cscale
    }

    fun centerviewdwent(sel: dwent) {
        //width/2
        scrollTo(
            (sel.disrect[0] + sel.disrect[2] - width).toInt() / 2,
            (sel.disrect[1] + sel.disrect[3] - height).toInt() / 2
        )
        //cenerview = true
    }

    fun centerviewdwent(h: Long): Boolean {
        rlst.forEach {
            if (it.handle == h) {
                println(it.disrect[0].toString() + "====" + it.disrect[2] + "====" + it.disrect[1] + "====" + it.disrect[3])
                println(((it.disrect[2] - it.disrect[0]) / 2 + it.disrect[0]).toString() + "====" + ((it.disrect[3] - it.disrect[1]) / 2 + it.disrect[1]))
                /* scrollTo(
                     (it.disrect[0] + it.disrect[2] - width).toInt() / 20,
                     (it.disrect[1] + it.disrect[3] - height).toInt() / 20
                 )*/
                scrollTo(
                    ((it.disrect[2] - it.disrect[0]) / 2 + it.disrect[0]).toInt() / 10,
                    ((it.disrect[3] - it.disrect[1]) / 2 + it.disrect[1]).toInt() / 10
                )
                /*scrollTo(
                    (it.disrect[2] - it.disrect[0]).toInt() / 20,
                    (it.disrect[3] - it.disrect[1]).t / 20
                )*/
                return true
            }
        }
        return false
    }

    fun centerviewdwent(r: Rect) {
        scrollTo(
            (r.left + r.right - width).toInt() / 2,
            (r.top + r.bottom - height).toInt() / 2
        )
    }

    fun hatchpathtest(c: Canvas, cx: Float, cy: Float) {
        val tpaint = Paint()
        tpaint.color = Color.RED
//        tpaint.style = Paint.Style.STROKE
        val tpt = Path()
        tpt.moveTo(cx - 100, cy - 100)
        tpt.rLineTo(100f, 0f)
        //tpt.rLineTo(0f, 100f)
//        tpt.addArc(cx-100, cy-100, cx+100, cy+100, 0f, 90f)
        //tpt.moveTo(cx, cy+100)
        //tpt.lineTo(cx, cy+100)
//        val npt = Path()
        //tpt.arcTo(cx-100, cy-100, cx+100, cy+100, 0f, 90f, false)
//        npt.arcTo(cx-100, cy-100, cx+100, cy+100, 0f, 90f, false)
//        tpt.addPath(npt)
//        tpt.lineTo(cx, cy)
//        tpt.close()
        c.drawPath(tpt, tpaint)
    }

    fun arc_to_bezier(
        cx: Double,
        cy: Double,
        rx: Double,
        ry: Double,
        start_angle: Double,
        sweep_angle: Double,
        curve: DoubleArray
    ) {
        val x0 = Math.cos(sweep_angle / 2.0);
        val y0 = Math.sin(sweep_angle / 2.0);
        val tx = (1.0 - x0) * 4.0 / 3.0;
        val ty = y0 - tx * x0 / y0;
        val px = DoubleArray(4);
        val py = DoubleArray(4);
        px[0] = x0;
        py[0] = -y0;
        px[1] = x0 + tx;
        py[1] = -ty;
        px[2] = x0 + tx;
        py[2] = ty;
        px[3] = x0;
        py[3] = y0;

        val sn = Math.sin(start_angle + sweep_angle / 2.0);
        val cs = Math.cos(start_angle + sweep_angle / 2.0);

        for (i in 0 until curve.size / 2) {
            curve[i * 2] = (cx + rx * (px[i] * cs - py[i] * sn));
            curve[i * 2 + 1] = (cy + ry * (px[i] * sn + py[i] * cs));
        }
    }

    var objscounta = HashMap<Int, Int>()
    var drawhatch = false
    var drawassitlien = false
    var fastdraw = true
    fun mdraw(canvas: Canvas) {
        if (isLoading)
            return

        fptscount = 0
        var dwentdcount = 0
        //313 hatch path,
        arrayOf(0, 3, 12, 15, 17, 18, 19, 25, 30, 31, 101, 202, 303, 313).forEach {
            objscounta[it] = 0
        }
        val drawms111 = measureTimeMillis {
            if (canvas != null) {
                var cenx = ((left + right) / 2.0f) + scrollX
                var ceny = ((top + bottom) / 2.0f) + scrollY
                //canvas.drawCircle(cenx, ceny, 3.0f, gpaint)
                if (selectMode && abs(mdx) > 0f) {
//                    println("drawselectrect: $mdx, $mdy  $mvx, $mvy")
                    canvas.drawRect(
                        mdx + scrollX,
                        mdy + scrollY,
                        mvx + scrollX,
                        mvy + scrollY,
                        gpaint
                    )
                }
//                canvas.save()
//                canvas.translate(cenx, ceny)
//                var rx = 100f
//                var ry = 100f
//                canvas.drawOval(-rx, -ry, rx, ry, mpaint)
//                canvas.save()
//                canvas.rotate(45f)
//                canvas.drawOval(-rx, -ry, rx, ry, mpaint)
//                canvas.restore()
//                val tpatht = Path()
//                //tpatht.moveTo(rx, 0f)
//                //tpatht.cubicTo(rx, ry, -rx, ry, -rx, 0f)
//                val cv = DoubleArray(8)
//                arc_to_bezier(0.0, 0.0, rx.toDouble(), ry.toDouble(), -Math.PI/4, Math.PI/2, cv)
//                var mtx = Matrix()
//                mtx.setRotate(45f)
//                val cfv = FloatArray(8){cv[it].toFloat()}
//                mtx.mapPoints(cfv)
//                tpatht.moveTo(cfv[0], cfv[1])
//                tpatht.cubicTo(cfv[2], cfv[3], cfv[4], cfv[5], cfv[6], cfv[7])
//                canvas.drawPath(tpatht, rpaint)
//                canvas.restore()
//                hatchpathtest(canvas, cenx, ceny)
//                canvas.drawArc(cenx, ceny, cenx+100, ceny+100, 90f, 180f, false, mpaint)

                canvas.scale(cscale, cscale, cenx, ceny)

                if (cenerview) {
                    var mapcx = (disrect[0] + disrect[2]) / 2
                    var mapcy = (disrect[1] + disrect[3]) / 2
                    scrollTo((mapcx - cenx).toInt(), (mapcy - ceny).toInt())
                    cenx += scrollX
                    ceny += scrollY
                    cenerview = false
                }

                val tw = width / cscale
                val th = height / cscale
                vr.left = (cenx - tw / 2).toInt()
                vr.top = (ceny - th / 2).toInt()
                vr.right = (cenx + tw / 2).toInt()
                vr.bottom = (ceny + th / 2).toInt()
                println("===vr.left:" + vr.left + "===" + vr.top + "===" + vr.right + "===" + vr.bottom)
                //canvas.drawRect(vr, gpaint)
                //canvas.drawCircle(vr.left.toFloat(), vr.top.toFloat(), 5f / cscale, gpaint)
                //canvas.drawCircle(vr.right.toFloat(), vr.bottom.toFloat(), 5f / cscale, gpaint)
//                canvas.drawRect(
//                    Rect(
//                        disrect[0].toInt() - 0,
//                        disrect[1].toInt() - 0,
//                        disrect[2].toInt() - 0,
//                        disrect[3].toInt() - 0
//                    ), rpaint
//                )

                if (!rlstready) return@measureTimeMillis

                var dr = Rect()
                rlst.forEach {
                    if (it == null) {
                        return@forEach
                    }
                    it.bneeddraw = false
                    if (it.skip) return@forEach
                    //                    fastdraw = false
                    if (fastdraw) {
                        dr.left = it.disrect[0].toInt()
                        dr.top = it.disrect[1].toInt()
                        dr.right = it.disrect[2].toInt()
                        dr.bottom = it.disrect[3].toInt()
                        if (Rect.intersects(vr, dr))
                        //是否太小，也不要绘制了
                            if (max(
                                    (it.disrect[2] - it.disrect[0]) * cscale,
                                    (it.disrect[3] - it.disrect[1]) * cscale
                                ) > 5
                            ) {
                                dwentdcount++
                                it.bneeddraw = true
                            }
                    } else {
                        //drawdwent(canvas, it, vr)
                        it.bneeddraw = true
                    }
                }

                rlst.forEach {
                    if (it == null) {
                        return@forEach
                    }
                    if (!it.bneeddraw) return@forEach

//                    if (dwentdcount > 1200)
//                        if (max(it.disrect[2] - it.disrect[0], it.disrect[3] - it.disrect[1]) * cscale < 25)
////                            if (dwentdcount % 4 != 0)
//                            return@forEach
                    it.selectf = sels.contains(it.handle)

                    if ((bshowdisrect || sels.contains(it.handle)) && (it.etype == 15)) {
                        canvas.drawRect(
                            it.disrect[0].toFloat() - 2,
                            it.disrect[1].toFloat() - 2,
                            it.disrect[2].toFloat() + 2,
                            it.disrect[3].toFloat() + 2,
                            rpaint
                        )
//                        if(it.etype == 15){
//                            bls[it.blk_name]?.ents?.forEach { itt ->
//                                canvas.drawRect(
//                                    itt.disrect[0].toFloat() - 2,
//                                    itt.disrect[1].toFloat() - 2,
//                                    itt.disrect[2].toFloat() + 2,
//                                    itt.disrect[3].toFloat() + 2,
//                                    rpaint
//                                )
//                            }
//                        }
                    }
                    drawdwent(canvas, it, vr, tscale = cscale)
                }

                //标记故障线段
//                if(true){
                faultLabelInfos.forEach {
                    if (handle2ithash.isEmpty()) {
                        handle2ithash.put(-1, dwent(-1, 0, "", 0, null))
//                        Thread{
                        inithandle2ithash()
//                        }.start()
                    }
                    if (handle2ithash.containsKey(it.objectHandle)) {
                        val tl = handle2ithash[it.objectHandle]
                        if (tl?.etype == 17) {
                            val tx = (tl.lwpolyline_fpts[0] + tl.lwpolyline_fpts[2]) / 2
                            val ty = (tl.lwpolyline_fpts[1] + tl.lwpolyline_fpts[3]) / 2
                            canvas.drawCircle(tx, ty, 20f / cscale, rpaint)
                        }
                    }
                }
            }
        }
        //println("ondraw $drawms ms entcount: $dwentdcount $objscounta")
    }

    val handle2ithash = HashMap<Long, dwent>()
    fun inithandle2ithash() {
        val ths = HashSet<Long>()
        /*DataHelper.objectsHandle.forEach { t, u ->
            ths.add(t)
        }*/
        rlst.forEach {
            if (ths.contains(it.handle))
                handle2ithash.put(it.handle, it)
        }
        if (handle2ithash.size > 1) handle2ithash.remove(-1)

        if (ths.size != handle2ithash.size)
            println("nofullmatch")
    }

    //val dwentlist = MutableList<dwent>(0){dwent(0,0,"")}
    var fptscount = 0
    val fpts = FloatArray(4 * 1024 * 8 * 8)

    //var linescount = 0
    var bshowdisrect = false
    var vr = Rect()
    override fun onDraw(canvas: Canvas?) {
        if (canvas != null) mdraw(canvas)
    }

    var needshowsd = 2.0
    var showcolor = true
    var hatchfill = false
    private var selectMode = false
    var lineselect = false
    fun drawdwent(canvas: Canvas, it: dwent, vr: Rect, oit: dwent? = null, tscale: Float = 1f) {
        objscounta[0] = objscounta[0]!!.plus(1)
        if (!(it.etype == 15 || it.etype == 19 || it.etype == 25)) {
            if (fastdraw && ((it.disrect[2] - it.disrect[0] + it.disrect[3] - it.disrect[1]) * abs(
                    tscale
                ) < needshowsd)
            ) return
            objscounta[it.etype] = objscounta[it.etype]!!.plus(1)
        }

        if (showcolor && it.color != 0) mpaint.color = it.color
        if (showcolor && oit != null && oit.etype == 15 && oit.blk_name.contains('*')) mpaint.color =
            oit.color

        if (it.selectf) {
            mpaint.strokeWidth = 5.0f / cscale
            mpaint.color = Color.RED
        } else {
            mpaint.strokeWidth = 0.0f
            mpaint.color = it.color
        }

        when (it.etype) {
            3 -> {      //DRW::CIRCLE
//                if((it.disrect[2]-it.disrect[0] + it.disrect[3] - it.disrect[1])*tscale < needshowsd) return
//                objscounta[it.etype] = objscounta[it.etype]!!.plus(1)

                canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), it.circle_r.toFloat(), mpaint)
            }
            12 -> {     //DRW::ELLIPSE
                canvas.save()

                if (drawassitlien) {
                    canvas.drawLine(
                        it.x.toFloat(),
                        it.y.toFloat(),
                        (it.x + it.line_sec_x).toFloat(),
                        (it.y + it.line_sec_y).toFloat(),
                        rpaint
                    )
                }

                canvas.rotate(
                    (it.blk_angle * 180 / Math.PI).toFloat(),
                    it.x.toFloat(),
                    it.y.toFloat()
                )
                if (drawassitlien) canvas.drawLine(
                    it.x.toFloat(),
                    it.y.toFloat(),
                    it.x.toFloat() + 100f,
                    it.y.toFloat(),
                    gpaint
                )

                if (drawassitlien) {
                    val dtr =
                        Math.sqrt((it.line_sec_x) * (it.line_sec_x) + (it.line_sec_y) * (it.line_sec_y))
                    canvas.drawOval(
                        (it.x - dtr * it.ellipls_ratio).toFloat(),
                        (it.y - dtr).toFloat(),
                        (2 * dtr * it.ellipls_ratio + it.x - dtr * it.ellipls_ratio).toFloat(),
                        (2 * dtr + it.y - dtr).toFloat(),
                        mpaint
                    )
                }

                if (!it.runcachflag) {
//                    it.runcachflag = true

                    var tsa = it.arc_startangle
                    var tse = it.arc_endangle

                    if (tsa < 0 && tse < 0 && tse > tsa && (!it.fromhatch)) {
                        tsa = -tsa
                        tse = -tse
                    }

                    tsa = caddegre2android(tsa, it)
                    tse = caddegre2android(tse, it)
                    var swa = tse - tsa

                    tsa = tsa * 180 / Math.PI
                    tse = tse * 180 / Math.PI
                    swa = swa * 180 / Math.PI
                    val tr =
                        Math.sqrt((it.line_sec_x) * (it.line_sec_x) + (it.line_sec_y) * (it.line_sec_y))

                    it.cachrectf = RectF(
                        (it.x - tr * it.ellipls_ratio).toFloat(),
                        (it.y - tr).toFloat(),
                        (2 * tr * it.ellipls_ratio + it.x - tr * it.ellipls_ratio).toFloat(),
                        (2 * tr + it.y - tr).toFloat()
                    )
                    it.tsa = tsa.toFloat()
                    it.swa = swa.toFloat()
                }

                if (drawhatch && oit?.hatchpaths != null) {
//                    if (false) {  //椭圆弧画法，有问题删去
//                        var mtx = Matrix()
//                        mtx.setRotate((it.blk_angle * 180 / Math.PI).toFloat(), it.x.toFloat(), it.y.toFloat())
//                        var tpss = Path()
//                        var trecf = RectF()
//                        //mtx.mapRect(trecf, it.cachrectf)
//                        tpss.addArc(it.cachrectf, it.tsa, it.swa)
//                        tpss.transform(mtx)
//                        oit.hatchpaths[0].addPath(tpss)
//                    }
                    if (false) {     //直线段画法，有问题删去
                        if (oit.ents[0] == it)
                            oit.hatchpaths[0].moveTo(
                                (it.x + it.arc_ps[0 + 4]).toFloat(),
                                (it.y + it.arc_ps[1 + 4]).toFloat()
                            )
                        oit.hatchpaths[0].lineTo(
                            (it.x + it.arc_ps[4 + 4]).toFloat(),
                            (it.y + it.arc_ps[5 + 4]).toFloat()
                        )
                        oit.hatchpaths[0].lineTo(
                            (it.x + it.arc_ps[2 + 4]).toFloat(),
                            (it.y + it.arc_ps[3 + 4]).toFloat()
                        )
                    }
                    //  cubicTo画法
                    if (oit.ents[0] == it)
                        oit.hatchpaths[0].moveTo(
                            (it.x + it.arc_ps[0 + 4]).toFloat(),
                            (it.y + it.arc_ps[1 + 4]).toFloat()
                        )
                    oit.hatchpaths[0].quadTo(
                        (it.x + it.arc_ps[4 + 4]).toFloat(),
                        (it.y + it.arc_ps[5 + 4]).toFloat(),
                        (it.x + it.arc_ps[2 + 4]).toFloat(),
                        (it.y + it.arc_ps[3 + 4]).toFloat()
                    )
//                    oit.hatchpaths[0].cubicTo(
//                        (it.x + it.arc_ps[0 + 4]).toFloat(),
//                        (it.y + it.arc_ps[1 + 4]).toFloat(),
//                        (it.x + it.arc_ps[4 + 4]).toFloat(),
//                        (it.y + it.arc_ps[5 + 4]).toFloat(),
//                        (it.x + it.arc_ps[2 + 4]).toFloat(),
//                        (it.y + it.arc_ps[3 + 4]).toFloat()
//                    )
//                    tpss.arcTo(it.cachrectf, it.tsa, it.swa)
//                    oit.hatchpaths[0].addArc(trecf, it.tsa, it.swa)
                } else
                    canvas.drawArc(
                        it.cachrectf,
                        it.tsa,
                        it.swa,
                        false, if (drawassitlien) rpaint else mpaint
                    )

                if (drawassitlien) {
                    //标记椭圆弧的启止点
                    if (it.arc_ps[0] != 0.0) {
                        canvas.drawCircle(
                            (it.x + it.arc_ps[0]).toFloat(),
                            (it.y + it.arc_ps[1]).toFloat(),
                            2f,
                            rpaint
                        )
                        canvas.drawCircle(
                            (it.x + it.arc_ps[2]).toFloat(),
                            (it.y + it.arc_ps[3]).toFloat(),
                            2f,
                            mpaint
                        )
                    }
                }

                canvas.restore()
            }
            15 -> {     //DRW::INSERT
                objscounta[it.etype] = objscounta[it.etype]!!.plus(1)
                if (bls.contains(it.blk_name)) {
                    val tb = bls[it.blk_name] as dwent
                    canvas.save()
                    canvas.translate(it.x.toFloat(), it.y.toFloat())
                    canvas.rotate((it.blk_angle * 180 / Math.PI).toFloat())
                    canvas.scale(it.blk_xscale.toFloat(), it.blk_yscale.toFloat())

                    (tb.ents as ArrayList<dwent>).forEach { itt ->
                        if (itt.etype != 101) drawdwent(
                            canvas,
                            itt,
                            vr,
                            it,
                            tscale = (tscale * it.blk_xscale).toFloat()
                        )
                    }
                    canvas.restore()

                    //对于块的属性而言，应该不要在用属性自身的值，而应直接用属性自己信息
//                    (tb.ents as ArrayList<dwent>).forEach { itt ->
//                        if (itt.etype == 101 && it.atts.keys.contains(itt.attr_name))
//                            drawdwent(canvas, it.atts[itt.attr_name]!!, vr, it)
//                    }
//                    if(it.atts != null && it.atts.keys.contains("NAME"))   //补充显示块的名称信息
//                        drawdwent(canvas, it.atts["NAME"]!!, vr, it)
//                    if (it.atts != null) it.atts.forEach { n, v -> drawdwent(canvas, v, vr, it) }
                    if (it.ents != null && it.ents.size > 0) it.ents.forEach { itt ->
                        drawdwent(
                            canvas,
                            itt,
                            vr,
                            it,
                            tscale = (tscale * it.blk_xscale).toFloat()
                        )
                    }
                }
            }
            17, 18 -> {       //DRW::LWPOLYLINE
                if (drawhatch && oit?.hatchpaths != null && oit.hatchpaths.size != 0) {
                    if (oit.ents[0] == it)
                        oit.hatchpaths[0].moveTo(it.lwpolyline_fpts[0], it.lwpolyline_fpts[1])
                    oit.hatchpaths[0].lineTo(it.lwpolyline_fpts[2], it.lwpolyline_fpts[3])
                } else
                    canvas.drawLines(it.lwpolyline_fpts, mpaint)
            }
            19, 25 -> {     //DRW::MTEXT DRW::TEXT
                //首先根据当前缩放率，判定textheight的大小，如果太小，就不要显示了。
                if (it.text_height * tscale < needshowsd) return
                objscounta[it.etype] = objscounta[it.etype]!!.plus(1)

                textPaint.textSize = it.text_height.toFloat() * 1f
//                if (mpaint.textSize > 120) return //太大了，有问题了
//                if (it.infos.size > 2 && it.infos[2].length > 0) {
                canvas.save()
                canvas.translate(it.x.toFloat(), it.y.toFloat())
                if (it.etype == 19 && it.mtext_attachpoint == "TopLeft".tomint())
                    canvas.translate(0f, it.text_height.toFloat())
//文字旋转
                canvas.rotate((it.blk_angle * 180 / Math.PI).toFloat())

                if (it.attr_widthfator > 0) canvas.scale(
                    it.attr_widthfator.toFloat(),
                    1f
                )
//                else canvas.scale(mpaint.textSize / 30f, mpaint.textSize / 30f)
//                canvas.drawText(it.textinfo, 0f, 0f, mpaint);
                hztxtdraw(canvas, it.textinfo, 0f, 0f, textPaint)
                canvas.restore()
//                }
            }
            30 -> {     //HATCH
                if (drawhatch) {
                    if (it.hatchpaths == null) {
                        val ofw = fastdraw
                        fastdraw = false
                        it.hatchpaths = ArrayList<Path>()
                        //else it.hatchpaths.clear()
                        //获取该hatch的paths
                        it.ents?.forEach { itt ->
                            drawdwent(canvas, itt, vr, it, tscale = tscale)
                        }
                        fastdraw = ofw
                    }
                    if (drawassitlien) {
                        it.hatchpaths.clear()
                        it.ents?.forEach { itt ->
                            drawdwent(canvas, itt, vr, it, tscale = tscale)
                        }
                    }
                    canvas.save()
                    canvas.scale(1f / tscale, 1f / tscale)
                    it.hatchpaths.forEach { ipath ->
                        val dpth = Path()
                        var mtx = Matrix()
                        mtx.setScale(tscale * 1, tscale * 1)
                        ipath.transform(mtx, dpth)

                        dpth.fillType = Path.FillType.EVEN_ODD
                        dpth.close()
                        val mpstyle = mpaint.style
                        if (hatchfill) mpaint.style = Paint.Style.FILL
                        canvas.drawPath(dpth, mpaint)
                        mpaint.style = mpstyle
                        dpth.reset()
                    }
                    canvas.restore()
                } else {
                    it.ents?.forEach { itt ->
                        drawdwent(canvas, itt, vr, it, tscale = tscale)
                    }
                }
            }
            31 -> {     //ARC
                if (drawassitlien) {
                    canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), it.circle_r.toFloat(), mpaint)
                    val tsa = (Math.PI * 2 - it.arc_startangle)
                    val tse = (Math.PI * 2 - it.arc_endangle)
                    canvas.drawCircle(
                        (it.x + it.circle_r * Math.cos(tsa)).toFloat(),
                        (it.y + it.circle_r * Math.sin(tsa)).toFloat(), 3f, rpaint
                    )
                    canvas.drawCircle(
                        (it.x + it.circle_r * Math.cos(tse)).toFloat(),
                        (it.y + it.circle_r * Math.sin(tse)).toFloat(), 2f, mpaint
                    )
                }
                if (drawhatch && oit?.hatchpaths != null) {
                    oit.hatchpaths[0].addArc(
                        RectF(
                            (it.x - it.circle_r).toFloat(),
                            (it.y - it.circle_r).toFloat(),
                            (it.x + it.circle_r).toFloat(),
                            (it.y + it.circle_r).toFloat()
                        ),
                        (360 - it.arc_startangle * 180 / Math.PI).toFloat(),
                        -((360 - (it.arc_startangle - it.arc_endangle) * 180 / Math.PI) % 360).toFloat()
                    )
                } else {
                    canvas.drawArc(
                        RectF(
                            (it.x - it.circle_r).toFloat(),
                            (it.y - it.circle_r).toFloat(),
                            (it.x + it.circle_r).toFloat(),
                            (it.y + it.circle_r).toFloat()
                        ),
                        (360 - it.arc_startangle * 180 / Math.PI).toFloat(),
                        -((360 - (it.arc_startangle - it.arc_endangle) * 180 / Math.PI) % 360).toFloat(),
                        false, if (drawassitlien) rpaint else mpaint
                    )
                }

                //println(it)
            }
            101, 202 -> {        //mblock attr
                textPaint.textSize = it.text_height.toFloat()
                if (textPaint.textSize < 5 || textPaint.textSize > 120) return  //太大或太小，就都不要显示了。
                canvas.save()
                canvas.translate(it.x.toFloat(), it.y.toFloat())
                if (oit == null) {
//                    canvas.save()

                    canvas.rotate((it.blk_angle * 180 / Math.PI).toFloat())
                    canvas.scale(it.attr_widthfator.toFloat(), 1f)
                    //canvas.drawText(if (it.etype == 101) it.attr_name else it.attr_val, 0f, 0f, mpaint)
                    hztxtdraw(
                        canvas,
                        if (it.etype == 101) it.attr_name else it.attr_val,
                        0f,
                        0f,
                        textPaint
                    )
//                    canvas.restore()
                } else {
//                    canvas.save()
                    if (it.attr_widthfator > 0) canvas.scale(it.attr_widthfator.toFloat(), 1f)
                    hztxtdraw(
                        canvas,
                        if (it.etype == 101) it.attr_name else it.attr_val,
                        0f,
                        0f,
                        textPaint
                    )
//                    canvas.restore()
                }
                canvas.restore()
            }
            303 -> {    //my hatch polyline paths
                val tps = Path()
//                    tps.moveTo(it.lwpolyline_pts[0].toFloat(), it.lwpolyline_pts[1].toFloat())
//                    tps.rLineTo(100f, 0f)
//                val cx = it.lwpolyline_pts[0].toFloat()
//                val cy = it.lwpolyline_pts[1].toFloat()
//                    tps.addArc(cx - 100, cy -100f, cx + 100, cy + 100, 0f, 180f)
//                    tps.addArc(cx - 100, cy -100f, cx + 100, cy + 100, 0f, -180f)
                //tps.addArc(cx - 100, cy -100f, cx + 100, cy + 100, 0f, -180f)
                var prex = 0.0
                var prey = 0.0
                for (i in 0 until it.lwpolyline_pts.size - 0 step 3) {
                    val tx = it.lwpolyline_pts[i + 0]
                    val ty = it.lwpolyline_pts[i + 1]
                    val tb = it.lwpolyline_pts[i + 2]

                    if (i == 0) {
                        if (drawhatch)
                            tps.moveTo(tx.toFloat(), ty.toFloat())
                        else {
                            prex = tx
                            prey = ty
                        }
                    }

                    if (tb == 0.0) {
                        if (drawhatch) {
                            tps.lineTo(tx.toFloat(), ty.toFloat())
                        } else {
//                            canvas.drawLine(prex.toFloat(), prey.toFloat(), tx.toFloat(), ty.toFloat(), mpaint)
                            prex = tx
                            prey = ty
                        }
                    } else {   //依据tb计算弧
                        val ca = Math.atan(tb) * 4
                        val x2 =
                            if (i < it.lwpolyline_pts.size - 3) it.lwpolyline_pts[i + 3 + 0] else it.lwpolyline_pts[0]
                        val y2 =
                            if (i < it.lwpolyline_pts.size - 3) it.lwpolyline_pts[i + 3 + 1] else it.lwpolyline_pts[1]
                        val pl = cal2pointlen(arrayOf(tx, ty, x2, y2).toDoubleArray())
                        var ra = (pl / 2) / Math.sin(ca / 2)

                        //依据圆上两点坐标及半径，计算圆心坐标
                        val x1 = tx
                        val y1 = ty
                        val c1 = (x2 * x2 - x1 * x1 + y2 * y2 - y1 * y1) / (2 * (x2 - x1))
                        val c2 = (y2 - y1) / (x2 - x1)  //斜率
                        val A = (c2 * c2 + 1)
                        val B = (2 * x1 * c2 - 2 * c1 * c2 - 2 * y1)
                        val C = x1 * x1 - 2 * x1 * c1 + c1 * c1 + y1 * y1 - ra * ra
                        var y = (-B + Math.sqrt(B * B - 4 * A * C)) / (2 * A)
                        if (y != y) y = (-B + Math.sqrt(Math.abs(B * B - 4 * A * C))) / (2 * A)
                        val x = c1 - c2 * y

                        val sa = Math.atan2(y1 - y, x1 - x) * 180 / Math.PI
                        val sb = Math.atan2(y2 - y, x2 - x) * 180 / Math.PI
//                            ra *= 10
                        ra = Math.abs(ra)
                        val rf =
                            RectF(
                                (x - ra).toFloat(),
                                (y - ra).toFloat(),
                                (x + ra).toFloat(),
                                (y + ra).toFloat()
                            )
                        if (drawhatch) tps.addArc(
                            rf,
                            sa.toFloat(),
                            (Math.abs(sb - sa) * if (tb > 0) 1 else -1).toFloat()
                        )
                        else {
                            canvas.drawArc(
                                rf,
                                sa.toFloat(),
                                (Math.abs(sb - sa) * if (tb > 0) 1 else -1).toFloat(),
                                false,
                                mpaint
                            )
//                            canvas.drawRect(rf, mpaint)
//                            canvas.drawArc(rf, abs(sa).toFloat(), (90).toFloat(), false, mpaint)
//                            canvas.drawArc(rf, 50f, 90f, true, mpaint)
                        }
                    }
                }
                oit?.hatchpaths?.add(tps)
            }
            313 -> {    //my hatch edage paths
                if (drawhatch && oit?.hatchpaths != null) {
                    if (oit.hatchpaths.size == 0) oit.hatchpaths.add(Path())
                    val tps = oit.hatchpaths[0]
                    //val tps = Path()
                    if (it.hatchpaths == null) {
                        it.hatchpaths = ArrayList<Path>()
//                        it.hatchpaths.add(Path())
                        it.hatchpaths.add(tps)
                        it.ents?.forEach { itt ->
                            drawdwent(canvas, itt, vr, it, tscale = tscale)
                        }
//                        oit.hatchpaths.add(it.hatchpaths[0])
                    }
//                    if (it.lwpolyline_pts != null) {
//                        for (i in 0 until it.lwpolyline_pts.size / 6) {
//                            tps.moveTo(it.lwpolyline_pts[i * 6 + 0].toFloat(), it.lwpolyline_pts[i * 6 + 1].toFloat())
//                            tps.lineTo(it.lwpolyline_pts[i * 6 + 3].toFloat(), it.lwpolyline_pts[i * 6 + 4].toFloat())
//                            if (drawassitlien)
//                                canvas.drawCircle(
//                                    it.lwpolyline_pts[i * 6 + 0].toFloat(),
//                                    it.lwpolyline_pts[i * 6 + 1].toFloat(),
//                                    2f,
//                                    rpaint
//                                )
//                        }
//                    }

//                    oit.hatchpaths.add(tps)
                }
                if (!drawhatch) {
                    it.ents?.forEach { itt ->
                        drawdwent(canvas, itt, vr, it, tscale = tscale)
                    }
                    if (it.lwpolyline_pts != null) {
                        for (i in 0 until it.lwpolyline_pts.size / 6) {
                            canvas.drawLine(
                                it.lwpolyline_pts[i * 6 + 0].toFloat(),
                                it.lwpolyline_pts[i * 6 + 1].toFloat(),
                                it.lwpolyline_pts[i * 6 + 3].toFloat(),
                                it.lwpolyline_pts[i * 6 + 4].toFloat(),
                                mpaint
                            )
                        }
                    }
                }
            }
        }
    }

    val percharwidth = 30f
    fun hztxtdraw(canvas: Canvas, textinfo: String, x: Float, y: Float, textPaint: TextPaint) {
        //val layout =
        //StaticLayout(textinfo, textPaint, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
        var x = x
        var y = y
        canvas.save()
        canvas.translate(x, y)

        canvas.scale(textPaint.textSize / 30f, textPaint.textSize / 30f)
        canvas.translate(0f, -38f)
        //println("ooooooooo***"+textinfo)
        var i = 0
        textinfo.forEach {
            if (hztxt.containsKey(it)) {
                if (it.toString() == "\n") {
                    y += 15
                    canvas.translate(
                        -i * percharwidth,
                        y
                    )
                    i = 0
                } else {
                    canvas.drawLines(hztxt[it]!!.second, textPaint)
                    canvas.translate(
                        if (hztxt[it]!!.first == 0) percharwidth else hztxt[it]!!.first.toFloat(),
                        0f
                    )
                    i++
                }
            } else {
                canvas.drawRect(3f, 3f, 27f, 27f, textPaint)
                canvas.translate(percharwidth, 0f)
            }
        }
        canvas.restore()
        //layout.draw(canvas)
    }




    fun LoadFiles(cadfileName: String) {
        if (true && rlst.size == 0) {

            try {
                loadcadinfos(cadfileName)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                print(ex.stackTrace)
            }
//            if(!fontInited)
//            {
//                Thread {
//                    try{
//                        getshxinfo(StationConfigs.LocalDataPath+"hztxt.dat", hztxt)
//                    }
//                    catch (ex:java.lang.Exception){
//                        print(ex.stackTrace)
//                    }
//                    isLoading = false
//
//                }.start()
//                fontInited = true
//            }
        }
    }

    fun ReloadFiles(cadName: String) {
        bls.clear()
        rlst.clear()
        isLoading = true
        // loadConfig()
        LoadFiles(cadName)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
        gbmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    }

    fun loadcadinfos(fileName: String) {
        rlstready = false
        isLoading = true
        rlst = ArrayList<dwent>()
        bls.clear()
        Thread() {
            var isOk = false
            try {
//                val cfile = File(cachfilename)
//                if(!cfile.exists()){
//                    buildCacheFile(cadFileName)
//                }


                //  2021.3.26改成读取assets文件夹，打包方便
                //getFirstCadFileName() +
                cachfileread(getFirstCadFileName() + "cache_" + fileName)
                if (!fontInited) {
                    getshxinfo(ctx, hztxt)
                    fontInited = true
                }
                isOk = true
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                print(ex.stackTrace)

            }
            isLoading = false
            rlstready = true

            ctx.runOnUiThread {
                showall()
                /* scrollTo(1000, 0)
                 setScale(0.1f)*/
                invalidate()

                longToast(if (isOk) "CAD文件加载成功." else "CAD文件加载失败！")
                //cadInterface!!.finishedDrawing()
                mCadViewHandler?.onLoadFinished()

            }
        }.start()
    }
    fun getFirstCadFileName(): String {
        //return FileSync.StationConfigs.getCADCacheFilePath(mConfigInfo!!.CadInfo[0].name)
        return Constants.BASEFILEURL + Constants.STATIONCODE + "cad/transformation/"
        // return "file:///android_asset/"
    }
    fun modifydisrect(it: dwent, pit: dwent? = null) {
        when (it.etype) {
            12 -> {       //ELLIPSE
                //rotateDisrect(it)
//                println()
                smodifydisrect(it)
            }
            15 -> {     //DRW::INSERT
                //if(it.ents != null)
                it.ents?.forEach { itt ->
                    modifydisrect(itt)
                }

                if (bls.contains(it.blk_name)) {
                    val tb = bls[it.blk_name] as dwent
                    if (tb.disrect[0] == Double.MAX_VALUE) {
                        modifydisrect(tb)
                    }
                    for (xii in 0 until tb.disrect.size)
                        it.disrect[xii] = tb.disrect[xii]

                    //此处需要根据块的旋转属性对块的显示范围进行粗修正
                    smodifydisrect(it)
                    it.skip = false
                } else {
                    it.skip = true      //对于未找到块信息的插入，直接跳过，不要处理了。
                }
            }
            25 -> {   //DRW::TEXT
                smodifydisrect(it)
            }
            30 -> {     //HATCH
                if (it.ents != null && it.ents.size > 0) {
                    it.ents.forEach { ittv ->
                        ittv.ents?.forEach { itt ->
                            //                            if (itt.etype == 17) return@forEach
                            if (itt.etype == 3) return@forEach
                            if (itt.etype == 31) return@forEach

                            if (itt.etype != 17)
                                smodifydisrect(itt)
                            ittv.disrect[0] = min(ittv.disrect[0], itt.disrect[0])
                            ittv.disrect[1] = min(ittv.disrect[1], itt.disrect[1])
                            ittv.disrect[2] = max(ittv.disrect[2], itt.disrect[2])
                            ittv.disrect[3] = max(ittv.disrect[3], itt.disrect[3])
                        }

                        it.disrect[0] = min(it.disrect[0], ittv.disrect[0])
                        it.disrect[1] = min(it.disrect[1], ittv.disrect[1])
                        it.disrect[2] = max(it.disrect[2], ittv.disrect[2])
                        it.disrect[3] = max(it.disrect[3], ittv.disrect[3])
                    }
                }
            }
//            31 -> {     //ARC
//                //smodifydisrect(it)
//            }
            101, 202 -> {    //my attrib
//                if (pit != null) it.disrect = arrayOf(0.0, 0.0, 0.0, 0.0)
                if (pit == null) {
                    //左底对齐
                    smodifydisrect(it)
                }
            }
            313 -> {
                println()
            }
            808 -> {    //my blkinfos
                it.ents.forEach { itt ->
                    if (itt.etype == 101) return@forEach //对于块的101是不用算显示区域的。
                    modifydisrect(itt)
                    it.disrect[0] = min(it.disrect[0], itt.disrect[0])
                    it.disrect[1] = min(it.disrect[1], itt.disrect[1])
                    it.disrect[2] = max(it.disrect[2], itt.disrect[2])
                    it.disrect[3] = max(it.disrect[3], itt.disrect[3])
                }
            }
        }
    }

    fun checkrlst() {
        rlst.forEach {
            if (it.skip)
                println()
        }
    }

    fun cachfileread(cachfilename: String) {
        bls.clear()
        rlst.clear()

        //  2021.3.26改成读取assets文件夹，打包方便
        // val dsi: DataInputStream = DataInputStream(ctx.getAssets().open(cachfilename))
        val dsi = DataInputStream(BufferedInputStream(FileInputStream(cachfilename)))
        for (i in 0 until disrect.size) disrect[i] = dsi.readDouble()
        for (i in 0 until dsi.readInt()) {    //blss
            val name = dsi.readUTF()
            bls.put(name, readdwent(dsi))
        }
        for (i in 0 until dsi.readInt()) {    //rlst

            rlst.add(readdwent(dsi))
        }
        dsi.close()
    }

    fun readdwent(dsi: DataInputStream): dwent {
        val e = dwent(0, 0, "", 0, null)
        e.handle = dsi.readLong()
        e.etype = dsi.readInt()

        //dso.writeInt(it.color)
        e.color = dsi.readInt()
        //dso.writeBoolean(it.skip)
        e.skip = dsi.readBoolean()
        //dso.writeDouble(it.x)
        e.x = dsi.readDouble()
        //dso.writeDouble(it.y)
        e.y = dsi.readDouble()
        //for (i in 0 until it.disrect.size) dso.writeDouble(it.disrect[i])
        for (i in 0 until e.disrect.size) e.disrect[i] = dsi.readDouble()

        //dso.writeDouble(it.line_sec_x)
        e.line_sec_x = dsi.readDouble()
        //dso.writeDouble(it.line_sec_y)
        e.line_sec_y = dsi.readDouble()

        //dso.writeDouble(it.circle_r)
        e.circle_r = dsi.readDouble()
        //dso.writeDouble(it.ellipls_ratio)
        e.ellipls_ratio = dsi.readDouble()
        //dso.writeUTF(it.textinfo)
        e.textinfo = dsi.readUTF()
        //dso.writeDouble(it.text_height)
        e.text_height = dsi.readDouble()
        //dso.writeInt(it.text_halign)
        e.text_halign = dsi.readInt()
        //dso.writeInt(it.text_valign)
        e.text_valign = dsi.readInt()
        //dso.writeInt(it.mtext_attachpoint)
        e.mtext_attachpoint = dsi.readInt()

        //dso.writeInt(it.lwpolyline_fpts.size)
        e.lwpolyline_fpts = FloatArray(dsi.readInt())
        //for (i in 0 until it.lwpolyline_fpts.size) dso.writeFloat(it.lwpolyline_fpts[i])
        for (i in 0 until e.lwpolyline_fpts.size) e.lwpolyline_fpts[i] = dsi.readFloat()

        val tfl = dsi.readInt()
        if (tfl > 0) {
            e.lwpolyline_pts = ArrayList<Double>()
            for (i in 0 until tfl) e.lwpolyline_pts.add(dsi.readDouble())
        }

        //dso.writeDouble(it.blk_xscale)
        e.blk_xscale = dsi.readDouble()
        //dso.writeDouble(it.blk_yscale)
        e.blk_yscale = dsi.readDouble()
        //dso.writeDouble(it.blk_angle)
        e.blk_angle = dsi.readDouble()
        //dso.writeUTF(it.blk_name)
        e.blk_name = dsi.readUTF()
        //dso.writeUTF(it.attr_name)
        e.attr_name = dsi.readUTF()
        //dso.writeUTF(it.attr_val)
        e.attr_val = dsi.readUTF()
        //dso.writeDouble(it.attr_widthfator)
        e.attr_widthfator = dsi.readDouble()
        e.arc_startangle = dsi.readDouble()
        e.arc_endangle = dsi.readDouble()
        e.CounterClockWise = dsi.readBoolean()
        e.fromhatch = dsi.readBoolean()

        if (e.etype == 12)
            for (i in 0..9)
                e.arc_ps[i] = dsi.readDouble()

        for (i in 0 until dsi.readInt()) {   //ents
            if (e.ents == null) e.ents = ArrayList<dwent>(0)
            e.ents.add(readdwent(dsi))
        }

        return e
    }

    var ScaleGestureTime: Long = 0L

    inner class ScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            //缩放比例
            var scale = detector.scaleFactor.toFloat()
            cscale *= scale
            cscale = Math.max(Math.min(cscale, 3f), minScale)
            this@skiaView.invalidate()
            ScaleGestureTime = System.currentTimeMillis()
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            //一定要返回true才会进入onScale()这个函数

            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {

        }
    }
}

fun String.tomint(): Int {
    when (this) {
        "Left" -> 1
        "Middle" -> 2
        "TopLeft" -> 3
    }

    return -1
}

interface CadViewListener {
    fun isObjectSelectable(objectHandle: Long): Boolean
    fun onSelectObject(objectHandle: Long)
    fun onSelectObjects(objectsHandle: HashSet<Long>)
    fun onClearSelectObject()
    fun onLoadFinished()
}

fun caddegre2android(cdegree: Double, it: dwent): Double {
    var cde = cdegree
    if (!it.fromhatch) {    //逆时针角度
        if (cde > 0) {
            cde -= Math.PI / 2
            cde = -cde
        } else {
            cde = -cde
            cde += Math.PI / 2
        }
    } else {
        if (it.CounterClockWise) {
            if (cde > 0) {
                cde -= Math.PI / 2
                cde = -cde
            } else {
                cde = -cde
                cde += Math.PI / 2
            }
        } else cde += Math.PI / 2
    }

    return cde
}

val cdpi = 10
val deltl = 5.0   //点到线的最小选定范围值

fun infostoit(
    it: dwent,
    bls: java.util.HashMap<String, dwent>,
    hztxt: HashMap<Char, Pair<Int, FloatArray>>,
    pit: dwent? = null
): Boolean {
    if (it.hasinfosit) return true
    it.hasinfosit = true
    //对于前引用的块信息，其特征为块信息的显示区域未初始化，此处在进行二次信息初始化时，为优化初始化速度，可以此为条件进行过滤
//        if(it.disrect[0] != Double.MAX_VALUE) return
    var mySplite = MySplite()
    it.infos = mySplite.splites(it.info.trim(), "\\$\\^").toList()

    if (it.infos.size > 2) {
        if (it.infos[0].length == 0)
            return false
//        println(it.info)
        try {
            it.x = it.infos[0].toDouble() * cdpi
            it.y = it.infos[1].toDouble() * cdpi
        } catch (e: Exception) {
            e.printStackTrace()
            println(it)
        }
    }
//    if (it.color != null) {
////        println(it.color)
//    }

    when (it.etype) {
        3 -> {    //DRW::CIRCLE
            /* if(it.infos.size<2){
                 return false
             }*/
            it.circle_r = it.infos[2].toDouble() * cdpi

            it.disrect[0] = it.x - it.circle_r
            it.disrect[1] = it.y - it.circle_r
            it.disrect[2] = 2 * it.circle_r + it.disrect[0]
            it.disrect[3] = 2 * it.circle_r + it.disrect[1]

            //对于不需要二次修正显示区域的对象，此处直接将已处理标志置位
            it.hasmodifydisrect = true
        }
        12 -> {      //DRW::ELLIPSE椭圆
            /*if (it.infos.size < 2) {
                return false
            }*/
            it.line_sec_x = it.infos[2].toDouble() * cdpi
            it.line_sec_y = it.infos[3].toDouble() * cdpi
            it.ellipls_ratio = it.infos[4].toDouble()

            val tr =
                Math.sqrt((it.line_sec_x) * (it.line_sec_x) + (it.line_sec_y) * (it.line_sec_y))
//            it.blk_angle = -Math.atan(it.line_sec_x / it.line_sec_y)
            it.blk_angle = -Math.atan2(it.line_sec_x, it.line_sec_y)
//            if (it.line_sec_y < 0) it.blk_angle -= Math.PI
            //it.blk_angle = Math.PI/4
            if (it.infos.size > 5) {
//                it.arc_startangle = -it.blk_angle
//                it.arc_endangle = Math.PI/2 - it.blk_angle
                //it.arc_startangle = (it.infos[5].toDouble() + it.blk_angle) % (2 * Math.PI)
                //it.arc_endangle = (it.infos[6].toDouble() + it.blk_angle) % (2 * Math.PI)
//                it.arc_startangle = 0.0 + Math.atan(it.ellipls_ratio)//Math.asin(Math.sin(Math.PI/4)*it.ellipls_ratio) //+ it.blk_angle / it.ellipls_ratio //Math.PI/4 //+ Math.PI/2 //+ it.blk_angle
//                it.arc_startangle = Math.PI - Math.atan(it.infos[5].toDouble()*it.ellipls_ratio)
//                it.arc_startangle = Math.PI + Math.PI*3/4
//                it.arc_endangle = Math.PI/4+ Math.PI/4//+ it.blk_angle / it.ellipls_ratio//Math.PI/4 //+ Math.PI/2//+ it.blk_angle
                it.arc_startangle = it.infos[5].toDouble()
                it.arc_endangle = it.infos[6].toDouble()
            }

//            if (it.arc_startangle < 0 && it.arc_endangle < 0)
//                println(it.info)

            cacalellispdisrect(it)
        }
        15 -> {     //DRW::INSERT
            it.blk_xscale = it.infos[2].toDouble()
            it.blk_yscale = it.infos[3].toDouble()
            it.blk_angle = -it.infos[4].toDouble()
            it.blk_name = it.infos[5].replace("--mblankm--", " ")

            it.ents.forEach { itt ->
                //insert 属性初始化
                it.hasinfosit = infostoit(itt, bls, hztxt)
                //atts仅仅是ents的一个快捷链接而已，此处不用初始化该结构，以后直接从ents中取吧。
            }

            if (bls.contains(it.blk_name)) {
                val tb = bls[it.blk_name] as dwent
//                if(!tb.hasinfosit){
                it.hasinfosit = infostoit(tb, bls, hztxt)
//                }
                //为实现后修正显示区域，此处就不要计算了。
//                for (xii in 0 until tb.disrect.size)
//                    it.disrect[xii] = tb.disrect[xii]
                //infos时就不要动disrect了
                //此处需要根据块的旋转属性对块的显示范围进行粗修正
//                smodifydisrect(it)
                it.skip = false
            } else {
                it.skip = true      //对于未找到块信息的插入，直接跳过，不要处理了。
                it.hasinfosit = false   //未能成功初始化，需要进行二次处理。
            }
        }
        17 -> {   //DRW::LINE
            /*if (it.infos.size < 2) {
                return false
            }*/

            it.line_sec_x = it.infos[2].toDouble() * cdpi
            it.line_sec_y = it.infos[3].toDouble() * cdpi

            it.disrect[0] = min(it.x, it.line_sec_x) - deltl
            it.disrect[1] = min(it.y, it.line_sec_y) - deltl
            it.disrect[2] = max(it.x, it.line_sec_x) + deltl
            it.disrect[3] = max(it.y, it.line_sec_y) + deltl

            it.lwpolyline_fpts = FloatArray(4)
            it.lwpolyline_fpts[0] = it.x.toFloat()
            it.lwpolyline_fpts[1] = it.y.toFloat()
            it.lwpolyline_fpts[2] = it.line_sec_x.toFloat()
            it.lwpolyline_fpts[3] = it.line_sec_y.toFloat()
            /* if (it.handle == 29993L) {
                 print("--------------")
             }*/
            it.hasmodifydisrect = true
        }
        18 -> {     //DRW::LWPOLYLINE
            it.lwpolyline_pts = ArrayList<Double>()
            it.infos.forEachIndexed { index, s ->
                /*if(TextUtils.isEmpty(s)){
                    return false
                }*/
                val tv = s.toDouble() * cdpi
                it.lwpolyline_pts.add(tv)
                if (index % 2 == 0) {
                    if (tv < it.disrect[0]) it.disrect[0] = tv
                    if (tv > it.disrect[2]) it.disrect[2] = tv
                } else {
                    if (tv < it.disrect[1]) it.disrect[1] = tv
                    if (tv > it.disrect[3]) it.disrect[3] = tv
                }
            }
            //需要将cad的连续点数组，转换成android canvas的点线段数组
            it.lwpolyline_fpts = FloatArray((it.lwpolyline_pts.size / 2 - 1) * 4) { index ->
                var gid = index / 4
                it.lwpolyline_pts[gid * 2 + (index % 4)].toFloat()
            }

            it.disrect[0] -= deltl
            it.disrect[1] -= deltl
            it.disrect[2] += deltl
            it.disrect[3] += deltl

            it.hasmodifydisrect = true
        }
        19 -> {  //DRW::MTEXT
            it.text_height = it.infos[2].toDouble() * cdpi * 1.0            //对于MTEXT此处为该字体的实际大小
            it.textinfo = it.infos[3]
            it.textinfo = it.textinfo.replace("--mblankm--", " ")
            //2020.5.16 过滤去除字体格式
            /*if(it.textinfo.startsWith("图")){
                print("==")
            }*/

            try {
                if (it.textinfo.contains("\\") && it.textinfo.contains(";")) {
                    val c = appearNumber(it.textinfo, "\\")
                    for (i in 1..c) {
                        if (it.textinfo.contains("\\") && it.textinfo.contains(";")) {
                            //if(it.textinfo.indexOf(";") + 1>=c)break
                            if (it.textinfo.indexOf("\\") < (it.textinfo.indexOf(";") + 1)) {
                                val p: String =
                                    it.textinfo.substring(
                                        it.textinfo.indexOf("\\"),
                                        it.textinfo.indexOf(";") + 1
                                    )
                                if (p.contains("\\P")) {
                                    it.textinfo = it.textinfo.replace(p, "huanhang")
                                } else {
                                    it.textinfo = it.textinfo.replace(p, "")
                                }
                            }
                        }
                    }
                    if (it.textinfo.contains("{") || it.textinfo.contains("}")) {
                        it.textinfo = it.textinfo.replace("{", "")
                        it.textinfo = it.textinfo.replace("}", "")
                    }
                    if (it.textinfo.contains("huanhang")) {
                        it.textinfo = it.textinfo.replace("huanhang", "\n")
                    }

                }
                //再次清洗过滤换行（目的是为了过滤不含";"的数据）
                if (it.textinfo.contains("\\P")) {
                    it.textinfo = it.textinfo.replace("\\P", "\n")
                }
            } catch (e: StringIndexOutOfBoundsException) {
                e.printStackTrace()
            }

            it.mtext_attachpoint = it.infos[4].tomint()
            it.attr_widthfator = it.infos[5].toDouble()
            //if(it.attr_widthfator < 0.0)
            if (it.infos.size >= 10) {
                var s = it.infos[9].split(",")
                if (s.size >= 2) {
                    if (s[0] == " 1") {
                        it.blk_angle = 0.0
                    } else if (s[1] == " 1") {
                        it.blk_angle = -Math.PI / 2
                    }
                }
            }

            it.disrect[0] = it.x
            it.disrect[1] = it.y
            it.disrect[2] = it.x + it.textinfo.length * it.text_height * it.attr_widthfator
            it.disrect[3] = it.y + it.text_height
        }
        25 -> {   //DRW::TEXT  CAD图上的文字
            it.text_height = it.infos[2].toDouble() * cdpi * 1.0    //对于TEXT需要乘以标准字体的默认高度
            it.textinfo = it.infos[3]
            it.textinfo = it.textinfo.replace("--mblankm--", " ")
            /* if(TextUtils.isEmpty(it.infos[4])){
                 print("====")
             }*/
            it.blk_angle = -it.infos[4].toDouble()
            it.attr_widthfator = it.infos[5].toDouble()
            it.text_halign = it.infos[6].tomint()
            it.text_valign = it.infos[7].tomint()

            //左底对齐
//            mpaint.textSize = it.text_height.toFloat()
            it.disrect[0] = 0.0
            it.disrect[1] = -it.text_height
//                it.disrect[2] = mpaint.measureText(it.textinfo) * it.attr_widthfator
//                it.disrect[2] = it.textinfo.length * 32f * (it.text_height / 30f) * it.attr_widthfator
            it.disrect[2] =
                hztxtmeasure(it.textinfo, hztxt) * (it.text_height / 30f) * it.attr_widthfator
            it.disrect[3] = 0.0

//            smodifydisrect(it)
        }
        30 -> {       //HATCH
            if (it.ents != null && it.ents.size > 0) {
                it.ents.forEach { itt -> infostoit(itt, bls, hztxt) }
            }
            it.lwpolyline_pts = ArrayList<Double>(0)
            var ci = 3
            if (it.infos.size > 4) {
                do {
                    val tpss2 = dwent(0, 313, "", it.color, null)
                    tpss2.lwpolyline_pts = ArrayList<Double>()
                    tpss2.ents = ArrayList<dwent>(0)
                    val edgscount = it.infos[ci].toInt()
                    for (i in 0 until edgscount) {
                        if (ci >= it.infos.size) break
                        when (it.infos[++ci]) {
                            "LineEdge" -> {     //该径路下的lineedage统一处理到tpss2的lwpolyline_pts数组中。
//                                for (si in 0..1) {
//                                    //init303infos(it, ci + 1 + si * 3, tpss2)
//                                    //因为path的各边界间的顺序性，所以不能统一放到lwpolyline_pts数组中，而需要分开处理，并保留边界间的顺序关系
//                                }
                                val tinf =
                                    "${it.infos[ci + 1]}\$^${it.infos[ci + 2]}\$^${it.infos[ci + 4]}\$^${it.infos[ci + 5]}"
                                val tee = dwent(0, 17, tinf, it.color, null)
                                infostoit(tee, bls, hztxt)
                                tpss2.ents.add(tee)
                                ci += 6
                            }
                            "EllipseEdge", "CoEllipseEdge" -> {
                                var tinf = ""
                                for (tin in 1..7) tinf += "${it.infos[ci + tin]}\$^"
                                val tee = dwent(0, 12, tinf, it.color, null)
                                tee.CounterClockWise =
                                    if (it.infos[ci] == "CoEllipseEdge") true else false
                                tee.fromhatch = true
                                infostoit(tee, bls, hztxt)
                                //it.ents.add(tee)
                                tpss2.ents.add(tee)
                                ci += 7
                            }
                            "ArcEdge", "CoArcEdge" -> {
                                var tinf = ""
                                for (tin in 1..5) tinf += "${it.infos[ci + tin]}\$^"
                                val tee = dwent(0, 31, tinf, it.color, null)
                                tee.CounterClockWise =
                                    if (it.infos[ci] == "CoArcEdge") true else false
                                tee.fromhatch = true
                                infostoit(tee, bls, hztxt)
                                //it.ents.add(tee)
                                tpss2.ents.add(tee) //将当前路径的图形信息入该路径集
                                ci += 5
                            }
                        }
                    }
                    if (edgscount > 0) it.ents.add(tpss2)
                    if ((ci + 1) < it.infos.size) {
                        if (it.infos[ci + 1] == "IsPolyline") {   //该路径为polyline
                            val tpss = dwent(0, 303, "", it.color, null)
                            tpss.lwpolyline_pts = ArrayList<Double>()
                            val tpc = it.infos[ci + 2].toInt()
                            for (i in 0 until tpc) {
                                init303infos(it, ci + 2 + i * 3 + 1, tpss)
                            }

                            it.ents.add(tpss)   //路径入填充对象的路径集
                            ci += tpc * 3 + 2
                        }
                    }
                } while (++ci < it.infos.size)
            }
            it.lwpolyline_fpts =
                FloatArray(it.lwpolyline_pts.size) { i -> it.lwpolyline_pts[i].toFloat() }
        }
        31 -> {       //ARC
            /*if(it.infos.size<2){
                return false
            }*/
            it.circle_r = it.infos[2].toDouble() * cdpi
            it.arc_startangle = it.infos[3].toDouble() % (2 * Math.PI)
            it.arc_endangle = it.infos[4].toDouble() % (2 * Math.PI)

            //此处需要对圆弧计算各端点的位置，然后判定显示区域，此处先简化处理如下
            val tsa = Math.PI * 2 - it.arc_startangle
            val tse = Math.PI * 2 - it.arc_endangle
            //-((360 - (it.arc_startangle - it.arc_endangle) * 180 / Math.PI) % 360).toFloat()

            val locs = MutableList<Double>(0) { 0.0 }
            locs.add(it.x + it.circle_r * Math.cos(tsa))
            locs.add(it.y + it.circle_r * Math.sin(tsa))
            locs.add(it.x + it.circle_r * Math.cos(tse))
            locs.add(it.y + it.circle_r * Math.sin(tse))

            var ttsa = (tsa % (Math.PI * 2))
            if (ttsa < 0) ttsa += Math.PI * 2
            var ttse = (tse % (Math.PI * 2))
            if (ttse < 0) ttse += Math.PI * 2
            for (i in 0..3) {
                val tca = Math.PI / 2 * i
                if (ttsa > ttse && (tca < ttse || tca > ttsa)) continue
                if (ttsa < ttse && (tca < ttse || tca > ttsa)) continue
                locs.add(it.x + it.circle_r * Math.cos(tca))
                locs.add(it.y + it.circle_r * Math.sin(tca))
            }

            //计算显示框尺寸
            locs.forEachIndexed { index, tv ->
                if (index % 2 == 0) {
                    if (tv < it.disrect[0]) it.disrect[0] = tv
                    if (tv > it.disrect[2]) it.disrect[2] = tv
                } else {
                    if (tv < it.disrect[1]) it.disrect[1] = tv
                    if (tv > it.disrect[3]) it.disrect[3] = tv
                }
            }
        }
        101, 202 -> {    //my attrib
            it.text_height = it.infos[2].toDouble() * cdpi * 1.0    //文本高度
            it.attr_name = it.infos[3].replace("--mblankm--", " ")
            it.attr_val = it.infos[4].replace("--mblankm--", " ")
            it.blk_angle = -it.infos[5].toDouble()
            it.attr_widthfator = it.infos[6].toDouble()

            if (pit != null) it.disrect = arrayOf(0.0, 0.0, 0.0, 0.0)
            else {   //对于块的101是不用算显示区域的。
                //左底对齐
//                mpaint.textSize = it.text_height.toFloat()
                it.disrect[0] = 0.0
                it.disrect[1] = -it.text_height
//                    it.disrect[2] = mpaint.measureText(it.attr_name) * it.attr_widthfator
//                    it.disrect[2] = it.attr_name.length * 32f * (it.text_height / 30f) * it.attr_widthfator
                it.disrect[2] =
                    hztxtmeasure(it.attr_name, hztxt) * (it.text_height / 30f) * it.attr_widthfator
                it.disrect[3] = 0.0

//                smodifydisrect(it)
            }
        }
        808 -> {    //my blkinfos
            it.ents.forEach { itt ->
                it.hasinfosit = infostoit(itt, bls, hztxt, it)

//                it.disrect[0] = min(it.disrect[0], itt.disrect[0])
//                it.disrect[1] = min(it.disrect[1], itt.disrect[1])
//                it.disrect[2] = max(it.disrect[2], itt.disrect[2])
//                it.disrect[3] = max(it.disrect[3], itt.disrect[3])
            }

//            it.ents.forEach {itt ->
//                if(itt.disrect[2] > 2000)
//                    println(itt)
//            }

//            if (it.disrect[0] == Double.MAX_VALUE) {
//                it.skip = true
//                println(it)
//            }
        }
    }
    return it.hasinfosit
}

/**
 * public int indexOf(int ch, int fromIndex)
 * 返回在此字符串中第一次出现指定字符处的索引，从指定的索引开始搜索
 *
 * @param srcText
 * @param findText
 * @return
 */
fun appearNumber(srcText: String, findText: String): Int {
    var count = 0
    var index = 0
    while (srcText.indexOf(findText, index).also { index = it } != -1) {
        index = index + findText.length
        count++
    }
    return count
}

fun init303infos(pit: dwent, offset: Int, tpss: dwent) {
    val tx = pit.infos[offset + 0].toDouble() * cdpi
    //it.lwpolyline_pts.add(tx)
    if (tx < tpss.disrect[0]) tpss.disrect[0] = tx
    if (tx > tpss.disrect[2]) tpss.disrect[2] = tx

    val ty = pit.infos[offset + 1].toDouble() * cdpi
    //it.lwpolyline_pts.add(ty)
    if (ty < tpss.disrect[1]) tpss.disrect[1] = ty
    if (ty > tpss.disrect[3]) tpss.disrect[3] = ty

    val bulge = pit.infos[offset + 2].toDouble()

    tpss.lwpolyline_pts.add(tx)
    tpss.lwpolyline_pts.add(ty)
    tpss.lwpolyline_pts.add(bulge)
}

fun cacalellispdisrect(it: dwent) {
    val locs = MutableList<Double>(0) { 0.0 }

    //计算初始值
    var tsa = it.arc_startangle
    var tse = it.arc_endangle

    if (tsa < 0 && tse < 0 && tse > tsa && (!it.fromhatch)) {
        tsa = -tsa
        tse = -tse
    }

    tsa = caddegre2android(tsa, it)
    tse = caddegre2android(tse, it)

    val tr = Math.sqrt((it.line_sec_x) * (it.line_sec_x) + (it.line_sec_y) * (it.line_sec_y))

    var dChangZhouAngle = 0.0
    val x0 = 0.0
    val y0 = 0.0
    val b = tr
    val a = b * it.ellipls_ratio

    //计算弧启点坐标
    var radian = Math.atan2(Math.sin(tsa), Math.cos(tsa) * it.ellipls_ratio)
    var dLiXin = Math.atan2(a * Math.sin(radian), b * Math.cos(radian)) //离心角
    var sax = a * Math.cos(dLiXin) * Math.cos(dChangZhouAngle) - b * Math.sin(dLiXin) * Math.sin(
        dChangZhouAngle
    ) + x0
    var say = a * Math.cos(dLiXin) * Math.sin(dChangZhouAngle) + b * Math.sin(dLiXin) * Math.cos(
        dChangZhouAngle
    ) + y0
    locs.add(sax)
    locs.add(say)
    it.arc_ps[0] = sax
    it.arc_ps[1] = say
    //补充计算旋转后的端点实际坐标
    dChangZhouAngle = it.blk_angle
    it.arc_ps[4] =
        a * Math.cos(dLiXin) * Math.cos(dChangZhouAngle) - b * Math.sin(dLiXin) * Math.sin(
            dChangZhouAngle
        ) + x0
    it.arc_ps[5] =
        a * Math.cos(dLiXin) * Math.sin(dChangZhouAngle) + b * Math.sin(dLiXin) * Math.cos(
            dChangZhouAngle
        ) + y0


    //计算弧终点坐标
    dChangZhouAngle = 0.0
    radian = Math.atan2(Math.sin(tse), Math.cos(tse) * it.ellipls_ratio)
    dLiXin = Math.atan2(a * Math.sin(radian), b * Math.cos(radian)) //离心角
    sax = a * Math.cos(dLiXin) * Math.cos(dChangZhouAngle) - b * Math.sin(dLiXin) * Math.sin(
        dChangZhouAngle
    ) + x0
    say = a * Math.cos(dLiXin) * Math.sin(dChangZhouAngle) + b * Math.sin(dLiXin) * Math.cos(
        dChangZhouAngle
    ) + y0
    locs.add(sax)
    locs.add(say)
    it.arc_ps[2] = sax
    it.arc_ps[3] = say
    //补充计算旋转后的端点实际坐标
    dChangZhouAngle = it.blk_angle
    it.arc_ps[6] =
        a * Math.cos(dLiXin) * Math.cos(dChangZhouAngle) - b * Math.sin(dLiXin) * Math.sin(
            dChangZhouAngle
        ) + x0
    it.arc_ps[7] =
        a * Math.cos(dLiXin) * Math.sin(dChangZhouAngle) + b * Math.sin(dLiXin) * Math.cos(
            dChangZhouAngle
        ) + y0
    //再算一个第三点坐标
    val tma = (tsa + tse) / 2
    radian = Math.atan2(Math.sin(tma), Math.cos(tma) * it.ellipls_ratio)
    dLiXin = Math.atan2(a * Math.sin(radian), b * Math.cos(radian)) //离心角
    it.arc_ps[8] =
        a * Math.cos(dLiXin) * Math.cos(dChangZhouAngle) - b * Math.sin(dLiXin) * Math.sin(
            dChangZhouAngle
        ) + x0
    it.arc_ps[9] =
        a * Math.cos(dLiXin) * Math.sin(dChangZhouAngle) + b * Math.sin(dLiXin) * Math.cos(
            dChangZhouAngle
        ) + y0


    dChangZhouAngle = 0.0
    //计算弧顶点坐标
    val alen = tse - tsa
    tsa = tsa % (Math.PI * 2)
    if (tsa < 0) tsa += Math.PI * 2
    tse = tse % (Math.PI * 2)
    if (tse < 0) tse += Math.PI * 2
    if (abs(tse - tsa) < 1E-10) tse = tsa

    for (i in 0..3) {
        val ttopt = Math.PI / 2 * i

        if (tse != tsa) {
            //计算弧顶点坐标
            if (alen > 0) { //顺时针
                if (tsa > tse && (ttopt < tsa && ttopt > tse)) continue
                if (tsa < tse && (ttopt < tsa || ttopt > tse)) continue
            } else {  //逆时针
                if (tsa > tse && (ttopt > tsa || ttopt < tse)) continue
                if (tsa < tse && (ttopt > tsa && ttopt < tse)) continue
            }
        }

        radian = Math.atan2(Math.sin(ttopt), Math.cos(ttopt) * it.ellipls_ratio)
        dLiXin = Math.atan2(a * Math.sin(radian), b * Math.cos(radian)) //离心角
        sax = a * Math.cos(dLiXin) * Math.cos(dChangZhouAngle) - b * Math.sin(dLiXin) * Math.sin(
            dChangZhouAngle
        ) + x0
        say = a * Math.cos(dLiXin) * Math.sin(dChangZhouAngle) + b * Math.sin(dLiXin) * Math.cos(
            dChangZhouAngle
        ) + y0
        locs.add(sax)
        locs.add(say)
    }

    //计算显示框尺寸
    locs.forEachIndexed { index, tv ->
        if (index % 2 == 0) {
            if (tv < it.disrect[0]) it.disrect[0] = tv
            if (tv > it.disrect[2]) it.disrect[2] = tv
        } else {
            if (tv < it.disrect[1]) it.disrect[1] = tv
            if (tv > it.disrect[3]) it.disrect[3] = tv
        }
    }
}

fun fromtheatetoxy(it: dwent, reqradio: Double) {

}

fun getshxinfo(context: Context, hztxt: HashMap<Char, Pair<Int, FloatArray>>) {
//    val hzfn = "/sdcard/Download/skiaviewdata/hztxt.dat"
    //if (!File(hzfn).exists()) return
    val istream = context.assets.open("hztxt.dat")
    val dsi = DataInputStream(BufferedInputStream(istream))
    while (dsi.available() > 0) {
        val ccode = dsi.readChar()
        var tcwidth = dsi.readInt()
        if (tcwidth == 0) tcwidth = 30
        val fsize = dsi.readInt()
        val fpts = FloatArray(fsize)
        for (i in 0 until fsize) fpts[i] = dsi.readFloat()
        hztxt.put(ccode, Pair(tcwidth, fpts))
    }
    dsi.close()
}

fun hztxtmeasure(tinfo: String, hztxt: HashMap<Char, Pair<Int, FloatArray>>): Int {
    var tw = 0
    tinfo.forEach {
        if (hztxt.containsKey(it)) tw += hztxt[it]!!.first
        else tw += 32
    }
    return tw
}

var glcos = ""
fun smodifydisrect(it: dwent) {
    if (glcos.length > 0) return
    if (it.hasmodifydisrect) return
    it.hasmodifydisrect = true

    if (it.disrect[0] == Double.MAX_VALUE) {
        //该对象的显示区域未初始化，需要处理下
        it.disrect = arrayOf(0.0, 0.0, 0.0, 0.0)
        println("inmodifydisrect errorinfo ${it.info}")
        return
    }

    var sm = Matrix()
    var rm = Matrix()
    rm.setRotate((it.blk_angle * 180 / Math.PI).toFloat())
    if (abs(it.blk_xscale) > 0.0) {
        sm.setScale(it.blk_xscale.toFloat(), it.blk_yscale.toFloat())
        rm.preConcat(sm)
    }

    var fa = FloatArray(8)
    fa[0] = it.disrect[0].toFloat()
    fa[1] = it.disrect[1].toFloat()
    fa[2] = it.disrect[0].toFloat()
    fa[3] = it.disrect[3].toFloat()
    fa[4] = it.disrect[2].toFloat()
    fa[5] = it.disrect[3].toFloat()
    fa[6] = it.disrect[2].toFloat()
    fa[7] = it.disrect[1].toFloat()
    rm.mapPoints(fa)
    it.disrect = arrayOf(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE)
    for (i in 0 until fa.size step 2) {
        //it.disrect[i] = fa[i].toDouble()
        if (fa[i].isInfinite() || fa[i + 1].isInfinite())
            println(it)
        if (fa[i] < it.disrect[0]) it.disrect[0] = fa[i].toDouble()
        if (fa[i] > it.disrect[2]) it.disrect[2] = fa[i].toDouble()

        if (fa[i + 1] < it.disrect[1]) it.disrect[1] = fa[i + 1].toDouble()
        if (fa[i + 1] > it.disrect[3]) it.disrect[3] = fa[i + 1].toDouble()
    }
    //修正基点
    it.disrect[0] += it.x
    it.disrect[1] += it.y
    it.disrect[2] += it.x
    it.disrect[3] += it.y
}

fun rotateDisrect(it: dwent) {
    var rm = Matrix()
    rm.setRotate((it.blk_angle * 180 / Math.PI).toFloat())
    var fa = FloatArray(8)
    fa[0] = it.disrect[0].toFloat()
    fa[1] = it.disrect[1].toFloat()
    fa[2] = it.disrect[0].toFloat()
    fa[3] = it.disrect[3].toFloat()
    fa[4] = it.disrect[2].toFloat()
    fa[5] = it.disrect[3].toFloat()
    fa[6] = it.disrect[2].toFloat()
    fa[7] = it.disrect[1].toFloat()
    rm.mapPoints(fa)
    it.disrect = arrayOf(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE)
    for (i in 0 until fa.size step 2) {
        //it.disrect[i] = fa[i].toDouble()
        if (fa[i].isInfinite() || fa[i + 1].isInfinite())
            println(it)
        if (fa[i] < it.disrect[0]) it.disrect[0] = fa[i].toDouble()
        if (fa[i] > it.disrect[2]) it.disrect[2] = fa[i].toDouble()

        if (fa[i + 1] < it.disrect[1]) it.disrect[1] = fa[i + 1].toDouble()
        if (fa[i + 1] > it.disrect[3]) it.disrect[3] = fa[i + 1].toDouble()
    }
}

fun cachfilewrite(
    tcachfilename: String,
    disrect: DoubleArray,
    bls: java.util.HashMap<String, dwent>,
    rlst: java.util.ArrayList<dwent>
) {
    val dso = DataOutputStream(BufferedOutputStream(FileOutputStream(tcachfilename)))
    for (i in 0 until disrect.size) dso.writeDouble(disrect[i]) //disrect

    dso.writeInt(bls.size)  //bls
    bls.forEach { n, e ->
        dso.writeUTF(n)
        writedwent(dso, e)
    }

    dso.writeInt(rlst.size) //rlst
    rlst.forEach { writedwent(dso, it) }
    dso.flush()
    dso.close()
}

fun writedwent(dso: DataOutputStream, it: dwent) {
    dso.writeLong(it.handle)
    dso.writeInt(it.etype)
    dso.writeInt(it.color)
    dso.writeBoolean(it.skip)
    dso.writeDouble(it.x)
    dso.writeDouble(it.y)
    for (i in 0 until it.disrect.size) dso.writeDouble(it.disrect[i])

    dso.writeDouble(it.line_sec_x)
    dso.writeDouble(it.line_sec_y)

    dso.writeDouble(it.circle_r)
    dso.writeDouble(it.ellipls_ratio)
    dso.writeUTF(it.textinfo)
    dso.writeDouble(it.text_height)
    dso.writeInt(it.text_halign)
    dso.writeInt(it.text_valign)
    dso.writeInt(it.mtext_attachpoint)

    if (it.lwpolyline_fpts != null) {
        dso.writeInt(it.lwpolyline_fpts.size)
        for (i in 0 until it.lwpolyline_fpts.size) dso.writeFloat(it.lwpolyline_fpts[i])
    } else dso.writeInt(0)

    if (it.lwpolyline_pts != null) {
        dso.writeInt(it.lwpolyline_pts.size)
        it.lwpolyline_pts.forEach { dso.writeDouble(it) }
    } else dso.writeInt(0)

    dso.writeDouble(it.blk_xscale)
    dso.writeDouble(it.blk_yscale)
    dso.writeDouble(it.blk_angle)
    dso.writeUTF(it.blk_name)
    dso.writeUTF(it.attr_name)
    dso.writeUTF(it.attr_val)
    dso.writeDouble(it.attr_widthfator)
    dso.writeDouble(it.arc_startangle)
    dso.writeDouble(it.arc_endangle)
    dso.writeBoolean(it.CounterClockWise)
    dso.writeBoolean(it.fromhatch)

    if (it.etype == 12) it.arc_ps.forEach { itt -> dso.writeDouble(itt) }

    if (it.ents != null && it.ents.size > 0) {
        dso.writeInt(it.ents.size)
        it.ents.forEach { itt -> writedwent(dso, itt) }
    } else dso.writeInt(0)
}

fun cal2pointlen(pa: DoubleArray): Double {
    return Math.sqrt((pa[0] - pa[2]) * (pa[0] - pa[2]) + (pa[1] - pa[3]) * (pa[1] - pa[3]))
}

//Cad图数据转换
fun processCadFile(context: Context, stxt: String, destcafile: String) {
    val destFile = File(destcafile)
    if (destFile.exists())
        destFile.delete()

    //val hztxt = HashMap<Char, Pair<Int, FloatArray>>()
    var bls = HashMap<String, dwent>()//块列表
    var rlst = ArrayList<dwent>()//元素列表
    var disrect =
        doubleArrayOf(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE)

    //getshxinfo(context, hztxt)
    val tes = Gson().fromJson<ArrayList<avfindo>>(
        stxt,
        object : TypeToken<ArrayList<avfindo>>() {}.type
    )

    tes.forEach {
        /*  if (it.handle == 29575L) {
              print("====")
          }*/
        var te = dwent(it.handle, it.et, it.infos, it.color, it.ents)
        if (te.etype == 808) {
            infostoit(te, bls, hztxt)
            if (te.ents.size > 0)
                bls.put(te.info, te)
        } else rlst.add(te)
    }

    bls.forEach {
        infostoit(it.value as dwent, bls, hztxt)
        modifydisrect(it.value, bls)
    }
    rlst.forEach {
        /* if (it.handle == 29575L) {
             print("====")
         }*/
        infostoit(it, bls, hztxt)
        modifydisrect(it, bls)
        if (!it.skip) {
            //求取视图显示范围
            if ((it.disrect[0]) < (disrect[0])) disrect[0] = it.disrect[0]
            if ((it.disrect[1]) < (disrect[1])) disrect[1] = it.disrect[1]
            if ((it.disrect[2]) > (disrect[2])) disrect[2] = it.disrect[2]
            if ((it.disrect[3]) > (disrect[3])) disrect[3] = it.disrect[3]
        }
    }
    cachfilewrite(destcafile, disrect, bls, rlst)
}


fun modifydisrect(it: dwent, bls: java.util.HashMap<String, dwent>, pit: dwent? = null) {
    when (it.etype) {
        12 -> {       //ELLIPSE
            //rotateDisrect(it)
//                println()
            smodifydisrect(it)
        }
        15 -> {     //DRW::INSERT
            //if(it.ents != null)
            it.ents?.forEach { itt ->
                modifydisrect(itt, bls)
            }

            if (bls.contains(it.blk_name)) {
                val tb = bls[it.blk_name] as dwent
                if (tb.disrect[0] == Double.MAX_VALUE) {
                    modifydisrect(tb, bls)
                }
                for (xii in 0 until tb.disrect.size)
                    it.disrect[xii] = tb.disrect[xii]

                //此处需要根据块的旋转属性对块的显示范围进行粗修正
                smodifydisrect(it)
                it.skip = false
            } else {
                it.skip = true      //对于未找到块信息的插入，直接跳过，不要处理了。
            }
        }
        25 -> {   //DRW::TEXT
            smodifydisrect(it)
        }
        30 -> {     //HATCH
            if (it.ents != null && it.ents.size > 0) {
                it.ents.forEach { ittv ->
                    ittv.ents?.forEach { itt ->
                        //                            if (itt.etype == 17) return@forEach
                        if (itt.etype == 3) return@forEach
                        if (itt.etype == 31) return@forEach

                        if (itt.etype != 17)
                            smodifydisrect(itt)
                        ittv.disrect[0] = min(ittv.disrect[0], itt.disrect[0])
                        ittv.disrect[1] = min(ittv.disrect[1], itt.disrect[1])
                        ittv.disrect[2] = max(ittv.disrect[2], itt.disrect[2])
                        ittv.disrect[3] = max(ittv.disrect[3], itt.disrect[3])
                    }

                    it.disrect[0] = min(it.disrect[0], ittv.disrect[0])
                    it.disrect[1] = min(it.disrect[1], ittv.disrect[1])
                    it.disrect[2] = max(it.disrect[2], ittv.disrect[2])
                    it.disrect[3] = max(it.disrect[3], ittv.disrect[3])
                }
            }
        }
//            31 -> {     //ARC
//                //smodifydisrect(it)
//            }
        101, 202 -> {    //my attrib
//                if (pit != null) it.disrect = arrayOf(0.0, 0.0, 0.0, 0.0)
            if (pit == null) {
                //左底对齐
                smodifydisrect(it)
            }
        }
        313 -> {
            println()
        }
        808 -> {    //my blkinfos
            it.ents.forEach { itt ->
                if (itt.etype == 101) return@forEach //对于块的101是不用算显示区域的。
                modifydisrect(itt, bls)
                it.disrect[0] = min(it.disrect[0], itt.disrect[0])
                it.disrect[1] = min(it.disrect[1], itt.disrect[1])
                it.disrect[2] = max(it.disrect[2], itt.disrect[2])
                it.disrect[3] = max(it.disrect[3], itt.disrect[3])
            }
        }
    }
}

data class FaultLabelInfo(var objectHandle: Long, var percent: Float, var direction: Int)