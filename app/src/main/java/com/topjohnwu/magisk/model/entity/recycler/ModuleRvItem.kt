package com.topjohnwu.magisk.model.entity.recycler

import android.content.res.Resources
import androidx.annotation.StringRes
import com.topjohnwu.magisk.R
import com.topjohnwu.magisk.databinding.ComparableRvItem
import com.topjohnwu.magisk.extensions.addOnPropertyChangedCallback
import com.topjohnwu.magisk.extensions.get
import com.topjohnwu.magisk.extensions.toggle
import com.topjohnwu.magisk.model.entity.module.Module
import com.topjohnwu.magisk.model.entity.module.Repo
import com.topjohnwu.magisk.redesign.module.ModuleViewModel
import com.topjohnwu.magisk.utils.KObservableField

class ModuleRvItem(val item: Module) : ComparableRvItem<ModuleRvItem>() {

    override val layoutRes: Int = R.layout.item_module

    val lastActionNotice = KObservableField("")
    val isChecked = KObservableField(item.enable)
    val isDeletable = KObservableField(item.remove)

    init {
        isChecked.addOnPropertyChangedCallback {
            when (it) {
                true -> {
                    item.enable = true
                    notice(R.string.disable_file_removed)
                }
                false -> {
                    item.enable = false
                    notice(R.string.disable_file_created)
                }
            }
        }
        isDeletable.addOnPropertyChangedCallback {
            when (it) {
                true -> {
                    item.remove = true
                    notice(R.string.remove_file_created)
                }
                false -> {
                    item.remove = false
                    notice(R.string.remove_file_deleted)
                }
            }
        }
        when {
            item.updated -> notice(R.string.update_file_created)
            item.remove -> notice(R.string.remove_file_created)
        }
    }

    fun toggle() = isChecked.toggle()
    fun toggleDelete() = isDeletable.toggle()

    private fun notice(@StringRes info: Int) {
        lastActionNotice.value = get<Resources>().getString(info)
    }

    override fun contentSameAs(other: ModuleRvItem): Boolean = item.version == other.item.version
            && item.versionCode == other.item.versionCode
            && item.description == other.item.description
            && item.name == other.item.name

    override fun itemSameAs(other: ModuleRvItem): Boolean = item.id == other.item.id
}

class RepoRvItem(val item: Repo) : ComparableRvItem<RepoRvItem>() {

    override val layoutRes: Int = R.layout.item_repo

    override fun contentSameAs(other: RepoRvItem): Boolean = item == other.item

    override fun itemSameAs(other: RepoRvItem): Boolean = item.id == other.item.id
}

class ModuleItem(val item: Module) : ComparableRvItem<ModuleItem>() {

    override val layoutRes = R.layout.item_module_md2

    val isEnabled = KObservableField(item.enable)
    val isRemoved = KObservableField(item.remove)
    val isUpdated get() = item.updated

    val isModified get() = item.remove || item.updated

    init {
        isEnabled.addOnPropertyChangedCallback {
            item.enable = it ?: return@addOnPropertyChangedCallback
        }
        isRemoved.addOnPropertyChangedCallback {
            item.remove = it ?: return@addOnPropertyChangedCallback
        }
    }

    fun toggle(viewModel: ModuleViewModel) {
        isEnabled.toggle()
        viewModel.updateState()
    }

    fun delete(viewModel: ModuleViewModel) {
        isRemoved.toggle()
        viewModel.updateState()
    }

    override fun contentSameAs(other: ModuleItem): Boolean = item.version == other.item.version
            && item.versionCode == other.item.versionCode
            && item.description == other.item.description
            && item.name == other.item.name

    override fun itemSameAs(other: ModuleItem): Boolean = item.id == other.item.id

}