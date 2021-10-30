package com.cago.viewmodels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cago.models.logic.Input
import com.cago.models.logic.Output
import com.cago.repository.PackController
import com.cago.repository.callbacks.Callback
import com.cago.utils.ErrorType
import com.cago.utils.InputType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PackViewModel(private val packController: PackController) : ViewModel() {

    class PackViewModelFactory(private val packManager: PackController) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PackViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PackViewModel(packManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    var pack = MutableLiveData<String?>()

    val inputsLiveData = MutableLiveData(packController.inputsList)
    val outputsLiveData = MutableLiveData(packController.outputsList)
    val editedInput = MutableLiveData<Int>()
    val message = MutableLiveData("")

    var activeInputIndex = -1
    fun getActiveInput() = try {
        packController.inputsList[activeInputIndex] as Input
    } catch (e: Exception) {
        message.postValue(packController.context
            .getString(ErrorType.ERROR_FIELD_NOT_CHOSEN.getResource()))
        null
    }

    var activeOutputIndex = -1
    fun getActiveOutput() = try {
        packController.outputsList[activeOutputIndex] as Output
    } catch (e: Exception) {
        message.postValue(packController.context
            .getString(ErrorType.ERROR_FIELD_NOT_CHOSEN.getResource()))
        null
    }


    fun openPack(bundle: Bundle) {
        viewModelScope.launch(Dispatchers.IO) {
            packController.openPack(bundle, object : Callback<File> {
                override fun success(data: File?) {
                    packController.setPack(data)
                    packController.loadPack()
                    updateInputs()
                    updateOutputs()
                    pack.postValue(bundle.getString("name"))
                }

                override fun failure(error: ErrorType?) {
                    pack.postValue(null)
                }
            })
        }
    }

    fun addInput(name: String, type: InputType): Boolean =
        if (packController.notContainsInput(name)) {
            packController.createInput(name, type)
            updateInputs()
            true
        } else false

    fun editInput(input: Input, name: String, type: InputType) =
        if (packController.notContainsInput(name, packController.inputsList.indexOf(input))) {
            packController.editInput(input, name, type)
            updateInputs()
            true
        } else false

    fun deleteInput(input: Input) {
        packController.deleteInput(input)
        updateInputs()
    }

    fun getInputIndex(input: Input) = packController.inputsList.indexOf(input)

    fun getInput(index: Int) = packController.inputsList[index]

    private fun updateInputs() {
        inputsLiveData.postValue(packController.inputsList)
    }

    fun addOutput(name: String, visible: Boolean, formula: String? = null) =
        if (packController.notContainsOutput(name)) {
            packController.createOutput(name, visible, formula ?: "")
            updateOutputs()
            true
        } else false

    fun editOutput(output: Output, name: String, visible: Boolean) =
        if (packController.notContainsOutput(name, packController.outputsList.indexOf(output))) {
            packController.editOutput(output, name, visible)
            updateOutputs()
            true
        } else false

    fun deleteOutput(output: Output) {
        packController.deleteOutput(output)
        updateOutputs()
    }

    fun getOutputIndex(output: Output) = packController.outputsList.indexOf(output)

    fun getConnections(index: Int) = packController.connectedOutputs(index)

    private fun updateOutputs() {
        outputsLiveData.postValue(packController.outputsList)
    }

    fun handleOutput(output: Output) {
        viewModelScope.launch(Dispatchers.IO) {
            packController.parseFormula(output)
        }
    }

    fun getDescription() = packController.description ?: ""
    fun setDescription(text: String) {
        packController.description = text
    }

    fun isOwnUser() = packController.own

    fun run() {
        viewModelScope.launch(Dispatchers.Default) {
            if (packController.outputsList.isNotEmpty()) {
                packController.updateAll()
                withContext(Dispatchers.Main) { updateOutputs() }
                packController.savePack()
            }
        }
    }

    fun update(position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            packController.update(position)
            withContext(Dispatchers.Main) {
                updateOutputs()
                editedInput.postValue(position)
            }
        }
    }

    fun closePack() {
        packController.close()
        activeInputIndex = -1
        activeOutputIndex = -1
    }

}
