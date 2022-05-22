****
Cago
****
Cago provides easier way to do calculations.

Packages
########
Cago has package controll system, that lets you manage your data easily. Everything what you do in app is interacting with **package**, you can create and edit your own package, upload it to cloud storage or use that was created by someone else. 

.. note:: Edit package can only its owner.

The indicator of package shows its state:

#. **green**: actual version of package is uploaded.
#. **blue**: not actual version of package is uploaded.
#. **gray**: no version of package is uploaded

Using
#####
Package represents mathematical function in which you can pass arguments and get result. **Input** tab is list of functions's arguments, that user can change, and **Output** tab is list of resulting data.

Edititng
########
Edititng is done by creating fields and setting their value. You can set value of current field, copy, edit or delete it. 

Formulas syntax
###############
Setting value of output field means defining a formula in which value of field will be calculated. To avoid errors you should folow this rules:

#. Use arethmetic operations: **+**, **-**, *****, **/**, **^** and brackets.
#. Write **[name]** to get value of input field with name **name**.
#. Write **<name>** to get value of output field **name**.

Examples of valid formula:
####
  [a]+5*([b]+10)
  
  <y>+<x>+[go]/5
  
  (2^<weight>)-5*([age]+1)
