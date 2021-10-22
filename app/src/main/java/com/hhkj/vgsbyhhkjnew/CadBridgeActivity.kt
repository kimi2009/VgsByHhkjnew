package com.hhkj.vgsbyhhkjnew

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import java.util.*
import com.hhkj.vgsbyhhkjnew.R

/**
 *
 * @ProjectName:    DeviceManagementClient
 * @Package:        com.hhkj.devicemanagementclient.activity
 * @ClassName:      CadBridgeActivity
 * @Description:
 * @Author:         D.Han
 * @CreateDate:     2020/5/11 14:30
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 * @Version:        1.0
 */
class CadBridgeActivity : Activity() {
    var tipDialog: QMUITipDialog? = null
    var mSelectedObjectsHandle = java.util.HashSet<Long>()
    private val mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog
    lateinit var flag: String
    lateinit var cadview: skiaView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadbridge_activity)
        tipDialog = QMUITipDialog.Builder(this@CadBridgeActivity)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
            .setTipWord("正在渲染，请稍后")
            .create()
        cadview= findViewById(R.id.cadview)
        //  if (this@CadBridgeActivity.isFinishing) return


        /* cadview.setCadInterfaceListener(CadInterface {

         })*/


        //DataHelper.initDevices(this)
    }

    override fun onResume() {
        super.onResume()
        tipDialog!!.show()
        cadview.setBackgroundColor(Color.BLACK)
        cadview.setCadViewListener(object : CadViewListener {
            override fun isObjectSelectable(objectHandle: Long): Boolean {
                // println("====objectHandle："+objectHandle)

                return false
            }

            override fun onSelectObject(objectHandle: Long) {


            }

            override fun onSelectObjects(objectsHandle: HashSet<Long>) {
                mSelectedObjectsHandle.clear()
                mSelectedObjectsHandle.addAll(objectsHandle)
                for (handle in mSelectedObjectsHandle) {
                    sout("${handle}")
                }
            }

            override fun onClearSelectObject() {

            }

            override fun onLoadFinished() {
                if (this@CadBridgeActivity.isFinishing) return


            }
        })

        cadview.ReloadFiles("测测测.avg")

    }

    fun sout(msg: String) {
        println("====$msg")
    }

    fun navigateToObject(objList: ArrayList<Long>) {
        cadview.clearSelected()
        cadview.addSelected(objList)
        /*if (cadview.centerviewdwent(objList.get(0))) {
            cadview.setScale(0.4f)
        } else {
            val msg = String.format("CAD图中没有该句柄：${objList.get(0)}，请联系维护人员！")
           // Toast.makeText(this@CadBridgeActivity, msg, Toast.LENGTH_LONG).show()     去掉提示
        }*/
    }


    protected fun goActivity(cls: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, cls)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        tipDialog?.dismiss()

        super.onDestroy()
    }
}