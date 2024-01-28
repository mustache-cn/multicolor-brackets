package cn.com.mustache.multicolor.brackets.settings.form

import cn.com.mustache.multicolor.brackets.MulticolorHighlighter
import cn.com.mustache.multicolor.brackets.settings.MulticolorSettings
import com.intellij.application.options.colors.*
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EventDispatcher
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel


class MulticolorOptionsPanel(
    private val options: ColorAndFontOptions,
    private val schemesProvider: SchemesPanel,
    private val category: String
) : OptionsPanel {

    private lateinit var rootPanel: JPanel
    private lateinit var optionsTree: Tree

    private lateinit var multicolor: JBCheckBox

    private lateinit var colorLabel1: JLabel
    private lateinit var colorLabel2: JLabel
    private lateinit var colorLabel3: JLabel
    private lateinit var colorLabel4: JLabel
    private lateinit var colorLabel5: JLabel

    private val colorLabels: Array<JLabel>

    private lateinit var color1: ColorPanel
    private lateinit var color2: ColorPanel
    private lateinit var color3: ColorPanel
    private lateinit var color4: ColorPanel
    private lateinit var color5: ColorPanel

    private val colors: Array<ColorPanel>

    private lateinit var gradientLabel: JLabel

    private val properties: PropertiesComponent = PropertiesComponent.getInstance()
    private val eventDispatcher: EventDispatcher<ColorAndFontSettingsListener> =
        EventDispatcher.create(ColorAndFontSettingsListener::class.java)

    init {
        colors = arrayOf(color1, color2, color3, color4, color5)
        colorLabels = arrayOf(colorLabel1, colorLabel2, colorLabel3, colorLabel4, colorLabel5)

        val actionListener = ActionListener {
            eventDispatcher.multicaster.settingsChanged()
            options.stateChanged()
        }
        multicolor.addActionListener(actionListener)
        for (c in colors) {
            c.addActionListener(actionListener)
        }

        options.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun settingsChanged() {
                if (!schemesProvider.areSchemesLoaded()) return
                if (optionsTree.selectedValue != null) {
                    // update options after global state change
                    processListValueChanged()
                }
            }
        })

        optionsTree.apply {
            isRootVisible = false
            model = DefaultTreeModel(DefaultMutableTreeTableNode())
            selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
            addTreeSelectionListener {
                if (schemesProvider.areSchemesLoaded()) {
                    processListValueChanged()
                }
            }
        }
    }

    override fun getPanel(): JPanel = rootPanel

    override fun addListener(listener: ColorAndFontSettingsListener) {
        eventDispatcher.addListener(listener)
    }

    override fun updateOptionsList() {
        fillOptionsList()
        processListValueChanged()
    }

    private data class DescriptionsNode(val multicolorName: String, val descriptions: List<TextAttributesDescription>) {
        override fun toString(): String = multicolorName
    }

    private fun fillOptionsList() {
        val nodes = options.currentDescriptions.asSequence()
            .filter { it is TextAttributesDescription && it.group == category }
            .map {
                val description = it as TextAttributesDescription
                val multicolorName = description.toString().split(":")[0]
                multicolorName to description
            }
            .groupBy { it.first }
            .map { (multicolorName, descriptions) ->
                DefaultMutableTreeNode(DescriptionsNode(multicolorName,
                    descriptions.asSequence().map { it.second }.toList().sortedBy { it.toString() })
                )
            }
        val root = DefaultMutableTreeNode()
        for (node in nodes) {
            root.add(node)
        }

        (optionsTree.model as DefaultTreeModel).setRoot(root)
    }

    private fun processListValueChanged() {
        var descriptionsNode = optionsTree.selectedDescriptions
        if (descriptionsNode == null) {
            properties.getValue(SELECTED_COLOR_OPTION_PROPERTY)?.let { preselected ->
                optionsTree.selectOptionByMulticolorName(preselected)
                descriptionsNode = optionsTree.selectedDescriptions
            }
        }

        descriptionsNode?.run {
            properties.setValue(SELECTED_COLOR_OPTION_PROPERTY, multicolorName)
            reset(multicolorName, descriptions)
        } ?: resetDefault()
    }

    private fun resetDefault() {
        multicolor.isEnabled = false
        multicolor.isSelected = false
        gradientLabel.text = "Assign each brackets its own color from the spectrum below:"

        for (i in 0 until minRange()) {
            colors[i].isEnabled = false
            colors[i].selectedColor = null
            colorLabels[i].isEnabled = false
        }
    }

    private fun reset(multicolorName: String, descriptions: List<TextAttributesDescription>) {
        val multicolorOn = MulticolorHighlighter.isMulticolorEnabled(multicolorName)

        multicolor.isEnabled = true
        multicolor.isSelected = multicolorOn
        gradientLabel.text = "Assign each ${multicolorName.toLowerCase()} its own color from the spectrum below:"

        for (i in 0 until minRange()) {
            colors[i].isEnabled = multicolorOn
            colorLabels[i].isEnabled = multicolorOn
            colors[i].selectedColor = descriptions.indexOfOrNull(i)?.multicolorColor
            descriptions.indexOfOrNull(i)?.let { eventDispatcher.multicaster.selectedOptionChanged(it) }
        }
    }


    override fun applyChangesToScheme() {
        val scheme = options.selectedScheme
        val (multicolorName, descriptions) = optionsTree.selectedDescriptions ?: return
        when (multicolorName) {
            MulticolorHighlighter.NAME_ROUND_BRACKETS,
            MulticolorHighlighter.NAME_ANGLE_BRACKETS,
            MulticolorHighlighter.NAME_SQUARE_BRACKETS,
            MulticolorHighlighter.NAME_SQUIGGLY_BRACKETS -> {
                MulticolorHighlighter.setMulticolorEnabled(multicolorName, multicolor.isSelected)

                for (i in 0 until minRange()) {
                    colors[i].selectedColor?.let { color ->
                        descriptions[i].multicolorColor = color
                        descriptions[i].apply(scheme)
                    }
                }
            }
        }
    }

    private fun minRange() = minOf(MulticolorSettings.instance.numberOfColors, 5)

    override fun processListOptions(): MutableSet<String> = mutableSetOf(
        MulticolorHighlighter.NAME_ROUND_BRACKETS,
        MulticolorHighlighter.NAME_SQUARE_BRACKETS,
        MulticolorHighlighter.NAME_SQUIGGLY_BRACKETS,
        MulticolorHighlighter.NAME_ANGLE_BRACKETS
    )

    override fun showOption(option: String): Runnable? = Runnable {
        optionsTree.selectOptionByMulticolorName(option)
    }

    override fun selectOption(typeToSelect: String) {
        optionsTree.selectOptionByType(typeToSelect)
    }

    companion object {
        private const val SELECTED_COLOR_OPTION_PROPERTY = "multicolor.selected.color.option.name"

        private var TextAttributesDescription.multicolorColor: Color?
            get() = externalForeground
            set(value) {
                externalForeground = value
            }

        private val Tree.selectedValue: Any?
            get() = (lastSelectedPathComponent as? DefaultMutableTreeNode)?.userObject

        private val Tree.selectedDescriptions: DescriptionsNode?
            get() = selectedValue as? DescriptionsNode

        private fun Tree.findOption(nodeObject: Any, matcher: (Any) -> Boolean): TreePath? {
            val model = model as DefaultTreeModel
            for (i in 0 until model.getChildCount(nodeObject)) {
                val childObject = model.getChild(nodeObject, i)
                if (childObject is DefaultMutableTreeNode) {
                    val data = childObject.userObject
                    if (matcher(data)) {
                        return TreePath(model.getPathToRoot(childObject))
                    }
                }

                val pathInChild = findOption(childObject, matcher)
                if (pathInChild != null) return pathInChild
            }

            return null
        }

        private fun Tree.selectOptionByMulticolorName(multicolorName: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode
                        && multicolorName.isNotBlank()
                        && data.multicolorName.contains(multicolorName, ignoreCase = true)
            })
        }

        private fun Tree.selectOptionByType(attributeType: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode && data.descriptions.any { it.type == attributeType }
            })
        }

        private fun Tree.selectPath(path: TreePath?) {
            if (path != null) {
                selectionPath = path
                scrollPathToVisible(path)
            }
        }
    }
}

private fun <E> List<E>.indexOfOrNull(idx: Int): E? = if (idx < this.size) this[idx] else null