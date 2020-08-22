package page.anim

import common.util.anim.AnimCE
import common.util.anim.ImgCut
import page.support.AbJTable
import java.awt.Component
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.text.JTextComponent

internal class ImgCutEditTable : AbJTable() {
    var anim: AnimCE? = null
    var ic: ImgCut? = null
    override fun editCellAt(row: Int, column: Int, e: EventObject): Boolean {
        val result: Boolean = super.editCellAt(row, column, e)
        val editor: Component = editorComponent
        if (editor == null || editor !is JTextComponent) return result
        if (e is KeyEvent) editor.selectAll()
        return result
    }

    override fun getColumnClass(c: Int): Class<*> {
        return if (lnk.get(c) == 5) String::class.java else Int::class.java
    }

    override fun getColumnCount(): Int {
        return strs.size
    }

    override fun getColumnName(c: Int): String {
        return strs[lnk.get(c)]
    }

    override fun getRowCount(): Int {
        return if (ic == null) 0 else ic.n
    }

    override fun getValueAt(r: Int, c: Int): Any {
        if (ic == null || r < 0 || c < 0 || r >= ic.n || c >= strs.size) return null
        if (lnk.get(c) == 0) return r
        return if (lnk.get(c) == 5) ic.strs.get(r) else ic.cuts.get(r).get(lnk.get(c) - 1)
    }

    override fun isCellEditable(r: Int, c: Int): Boolean {
        return lnk.get(c) != 0
    }

    @Synchronized
    override fun setValueAt(`val`: Any, r: Int, c: Int) {
        var c = c
        if (ic == null) return
        c = lnk.get(c)
        if (c == 5) {
            ic.strs.get(r) = (`val` as String).trim { it <= ' ' }
            anim.unSave("imgcut edit name")
            return
        }
        var v = `val` as Int
        if (v < 0) v = 0
        if (c > 2 && v == 0) v = 1
        ic.cuts.get(r).get(c - 1) = v
        anim.unSave("imgcut edit data")
        anim.ICedited()
    }

    fun setCut(au: AnimCE?) {
        if (cellEditor != null) cellEditor.stopCellEditing()
        anim = au
        ic = if (au == null) null else au.imgcut
    }

    companion object {
        private const val serialVersionUID = 1L
        private val strs = arrayOf("id", "x", "y", "w", "h", "name")
    }
}
