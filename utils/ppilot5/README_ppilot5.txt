utiliser ppilot5
----------------

ppilot permet d’utiliser des systèmes de synthèse vocale compatibles SAPI5.

Lancement
ppilot5 -b 127.255.255.255:2010 -r Virginie -o "ScanSoft Virginie_Dri40_16kHz"

  -b adresse IP + port
  -r nom sous lequel apparaîtra l’agent sous ivy
  -o nom du moteur de synthèse utilisé

Commandes
  * Synthèse
    - ppilot Say="hello" ppilot prononce "hello" et envoie ppilot Answer=Finished quand le buffer est vide

  * Commandes
    - ppilot Command=Stop la synthèse vocale devrait être stoppée.
    - ppilot Command=Pause la synthèse vocale est mise en pause. ppilot renvoie ppilot Answer=Paused
    - ppilot Command=Resume la synthèse vocale est relancée si elle était en pause précédemment. ppilot renvoie ppilot Answer=Resumed
    - ppilot Command=Quit l’application se ferme

  * Paramètres
    - ppilot Param=Pitch:value le pitch devrait être changé par la valeur donnée. (ne fonctionne pas !)
    - ppilot Param=Speed:value la vitesse est changée par la valeur donnée. ppilot renvoie ppilot Answer=SpeedValueSet:value
    - ppilot Param=Volume:value le volume est changé par la valeur donnée. ppilot renvoie ppilot Answer=VolumeValueSet:value

    