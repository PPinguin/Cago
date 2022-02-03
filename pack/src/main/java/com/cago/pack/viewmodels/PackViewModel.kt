package com.cago.pack.viewmodels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cago.core.models.logic.Input
import com.cago.core.models.logic.Output
import com.cago.core.repository.PackController
import com.cago.core.repository.callbacks.Callback
import com.cago.core.utils.BaseViewModel
import com.cago.core.utils.ErrorType
import com.cago.core.utils.InputType
import com.cago.core.utils.StringProvider
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

class PackViewModel @Inject constructor(
    stringProvider: StringProvider,
    private val packController: PackController
) : BaseViewModel(stringProvider) {

    var pack = MutableLiveData<String?>()

    val inputsLiveData = MutableLiveData(packController.inputsList)
    val outputsLiveData = MutableLiveData(packController.outputsList)
    val editedInput = MutableLiveData<Int>()
    
    private val editedOutputs = mutableSetOf<Int>()

    var activeInputIndex = -1
    var changed = false
    
    fun getActiveInput() = try {
        packController.inputsList[activeInputIndex] as Input
    } catch (e: Exception) {
        message.postValue(stringProvider.get(ErrorType.ERROR_FIELD_NOT_CHOSEN.getResource()))
        null
    }

    var activeOutputIndex = -1
    fun getActiveOutput() = try {
        packController.outputsList[activeOutputIndex] as Output
    } catch (e: Exception) {
        message.postValue(stringProvider.get(ErrorType.ERROR_FIELD_NOT_CHOSEN.getResource()))
        null
    }


    fun openPack(bundle: Bundle) {
        viewModelScope.launch(Dispatchers.IO) {
            packController.openPack(bundle, object : Callback<File> {
                override fun success(data: File?) {
                    loadPack(bundle.getString("name"), data)
                }

                override fun failure(error: ErrorType?) {
                    pack.postValue(null)
                }
            })
        }
    }
    
    fun loadPack(name: String?, file: File?){
        viewModelScope.launch(Dispatchers.Default){
            packController.setPack(file)
            packController.loadPack()
            updateInputs()
            updateOutputs()
            pack.postValue(name)
            changed = false
        }
    }
    
    private fun changed(){changed = true}

    fun addInput(name: String, type: InputType): Boolean =
        if (packController.notContainsInput(name)) {
            packController.createInput(name, type)
            updateInputs()
            changed()
            true
        } else false

    fun editInput(input: Input, name: String, type: InputType) =
        if (packController.notContainsInput(name, packController.inputsList.indexOf(input))) {
            packController.editInput(input, name, type)
            updateInputs()
            changed()
            true
        } else false

    fun deleteInput(input: Input) {
        packController.deleteInput(input)
        updateInputs()
        changed()
    }
    
    fun getInput(index: Int) = packController.inputsList[index]

    private fun updateInputs() {
        inputsLiveData.postValue(packController.inputsList)
    }

    fun addOutput(name: String, visible: Boolean, formula: String? = null) =
        if (packController.notContainsOutput(name)) {
            packController.createOutput(name, visible, formula ?: "")
            updateOutputs()
            changed()
            true
        } else false

    fun editOutput(output: Output, name: String, visible: Boolean) =
        if (packController.notContainsOutput(name, packController.outputsList.indexOf(output))) {
            packController.editOutput(output, name, visible)
            updateOutputs()
            changed()
            true
        } else false

    fun deleteOutput(output: Output) {
        packController.deleteOutput(output)
        updateOutputs()
        changed()
    }

    fun getOutputIndex(output: Output) = packController.outputsList.indexOf(output)

    fun getConnections(index: Int) = packController.connectedOutputs(index)

    private fun updateOutputs() {
        outputsLiveData.postValue(packController.outputsList)
    }

    fun handleOutput(output: Output) =
        viewModelScope.launch(Dispatchers.Default) {
            changed()
            editedOutputs.add(packController.outputsList.indexOf(output))
            packController.handleOutput(output)
        }

    fun getDescription() = packController.description ?: ""
    fun setDescription(text: String) {
        packController.description = text
    }

    fun isOwnUser() = packController.own

    fun run() =
        viewModelScope.launch(Dispatchers.Default) {
            editedOutputs.forEach { packController.updateOO(it) }
            editedOutputs.clear()
            withContext(Dispatchers.Main) { updateOutputs() }
        }

    fun update(position: Int) =
        viewModelScope.launch(Dispatchers.Default) {
            packController.updateIO(position)
            withContext(Dispatchers.Main) {
                updateOutputs()
                editedInput.postValue(position)
            }
        }

    @DelicateCoroutinesApi
    fun closePack(save: Boolean) {
        GlobalScope.launch {
            if(save) packController.savePack()
            packController.close()
        }
        activeInputIndex = -1
        activeOutputIndex = -1
    }
}
