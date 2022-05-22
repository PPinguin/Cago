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
                    loadPack(bundle.getString("name"), data!!)
                }

                override fun failure(error: ErrorType?) {
                    if(error != null) message(error.getResource())
                    pack.postValue(null)
                }
            })
        }
    }
    
    fun loadPack(name: String?, file: File){
        viewModelScope.launch(Dispatchers.Default){
            packController.setPack(file)
            packController.loadPack()
            updateInputs()
            updateOutputs()
            pack.postValue(name)
            changed = false
        }
    }
    
    private fun changed(){changed = packController.isOwn()}

    fun addInput(name: String, type: InputType): Boolean {
        if (packController.notContainsInput(name)) {
            try{
                packController.createInput(name, type)
            } catch (e: Exception){ return false } 
            updateInputs()
            changed()
            return true
        } else return false
    }

    fun editInput(input: Input, name: String, type: InputType): Boolean {
        if (packController.notContainsInput(name, packController.inputsList.indexOf(input))) {
            try{
                packController.editInput(input, name, type)
            } catch(e: Exception){ return false }
            updateInputs()
            changed()
            return true
        } else return false
    }

    fun deleteInput(input: Input) = 
        viewModelScope.launch(Dispatchers.Default) { 
            packController.deleteInput(input)
            withContext(Dispatchers.Main){ updateInputs() }
            changed()
        }
    
    fun defaultInput(input: Input){
        viewModelScope.launch(Dispatchers.Default) {
            input.updateDefault()
            val index = packController.inputsList.indexOf(input)
            packController.getInputConnections(index).forEach{ addEditedOuput(it) }
            changed()
        }
    }
    
    fun getInput(index: Int) = packController.inputsList[index]

    private fun updateInputs() {
        inputsLiveData.postValue(packController.inputsList)
    }
    
    fun addOutput(name: String, visible: Boolean, formula: String? = null): Boolean {
        if (packController.notContainsOutput(name)) {
            try{
                packController.createOutput(name, visible, formula ?: "")
            } catch(e: Exception){ return false }
            updateOutputs()
            changed()
            return true
        } else return false
    }

    fun editOutput(output: Output, name: String, visible: Boolean): Boolean {
        if (packController.notContainsOutput(name, packController.outputsList.indexOf(output))) {
            try{
                packController.editOutput(output, name, visible)
            } catch (e: Exception){ return false }
            updateOutputs()
            changed()
            return true
        } else return false
    }

    fun deleteOutput(output: Output) =
        viewModelScope.launch(Dispatchers.Default) {
            packController.deleteOutput(output)
            withContext(Dispatchers.Main){ updateOutputs() }
            changed()
        }

    fun getOutputIndex(output: Output) = packController.outputsList.indexOf(output)

    fun getConnections(index: Int) = packController.connectedOutputs(index)

    private fun updateOutputs() {
        outputsLiveData.postValue(packController.outputsList)
    }
    
    private fun addEditedOuput(index: Int){
        val connects = packController.getOutputConnections(index)
        editedOutputs.removeAll { connects.contains(it) }
        editedOutputs.add(index)
    }

    fun handleOutput(output: Output) =
        viewModelScope.launch(Dispatchers.Default) {
            changed()
            val index = packController.outputsList.indexOf(output)
            addEditedOuput(index)
            packController.handleOutput(output)
        }

    fun getDescription() = packController.description ?: ""
    fun setDescription(text: String) {
        packController.description = text
        changed()
    }
    
    fun run() {
        if(changed) {
            viewModelScope.launch(Dispatchers.Default) {
                editedOutputs.forEach { packController.updateOO(it) }
                editedOutputs.clear()
                packController.extractVisibleOutputs()
                withContext(Dispatchers.Main) { updateOutputs() }
            }
        }
    }

    fun update(position: Int) =
        viewModelScope.launch(Dispatchers.Default) {
            packController.updateIO(position)
            withContext(Dispatchers.Main) {
                editedInput.postValue(position)
            }
        }

    @DelicateCoroutinesApi
    fun closePack(save: Boolean, exit: () -> Unit) = 
        GlobalScope.launch(Dispatchers.IO) {
            if(save) packController.savePack()
            packController.close()
            activeInputIndex = -1
            activeOutputIndex = -1
            withContext(Dispatchers.Main) {
                exit()
            }
        }
}
