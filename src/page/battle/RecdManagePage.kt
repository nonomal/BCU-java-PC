package page.battle

import common.util.stage.Recd
import main.MainBCU
import page.JBTN
import page.JTF
import page.Page
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.io.File
import java.util.function.Consumer
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class RecdManagePage(p: Page?) : AbRecdPage(p, true) {
    private val dele: JBTN = JBTN(0, "rem")
    private val rena: JTF = JTF()
    private val jlr: JList<Recd> = JList<Recd>()
    private val jspr: JScrollPane = JScrollPane(jlr)
    override fun getSelection(): Recd {
        return jlr.selectedValue
    }

    override fun resized(x: Int, y: Int) {
        super.resized(x, y)
        Page.Companion.set(jspr, x, y, 50, 100, 500, 1100)
        Page.Companion.set(dele, x, y, 600, 400, 300, 50)
        Page.Companion.set(rena, x, y, 600, 500, 300, 50)
    }

    override fun setList() {
        change(true)
        val r: Recd = jlr.selectedValue
        jlr.setListData(Recd.Companion.map.values.toTypedArray())
        jlr.setSelectedValue(r, true)
        setRecd(r)
        change(false)
    }

    override fun setRecd(r: Recd?) {
        super.setRecd(r)
        dele.isEnabled = r != null
        rena.isEditable = r != null
        rena.text = if (r == null) "" else r.name
    }

    private fun addListeners() {
        jlr.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (isAdj() || jlr.valueIsAdjusting) return
                setRecd(jlr.selectedValue)
            }
        })
        rena.setLnr(Consumer<FocusEvent> { x: FocusEvent? ->
            if (isAdj() || jlr.valueIsAdjusting) return@setLnr
            val r: Recd = jlr.selectedValue ?: return@setLnr
            val f = File("./replay/" + r.name + ".replay")
            if (f.exists()) {
                var str: String = MainBCU.validate(rena.text.trim { it <= ' ' })
                str = Recd.Companion.getAvailable(str)
                if (f.renameTo(File("./replay/$str.replay"))) {
                    Recd.Companion.map.remove(r.name)
                    r.name = str
                    Recd.Companion.map.put(r.name, r)
                }
            }
            rena.text = r.name
        })
        dele.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val r: Recd = jlr.selectedValue
                val f = File("./replay/" + r.name + ".replay")
                if (f.exists()) f.delete()
                if (!f.exists()) {
                    Recd.Companion.map.remove(r.name)
                    setList()
                }
                setRecd(null)
            }
        })
    }

    private fun ini() {
        add(jspr)
        add(dele)
        add(rena)
        addListeners()
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        preini()
        ini()
        resized()
    }
}
