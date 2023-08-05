# ShiftLabEntryTask
test task given by shift lab

### Инструкция по запуску

Версия Java - Oracle OpenJDK version 17.0.2
Входные параметры:
1. режим сортировки (необязательный) - по возрастанию (ascending) или по убыванию (descending) (соответственно)
-a или -d
2. тип данных (обязательный) - строки (string) или целые числа (integer) (соответственно)
-s или -i
3. имя выходного файла (обязательный)
4. имена входных файлов (обязательный, не менее одного)

### Особенности реализации

В задании сказано, что программа должна сортировать слиянием *несколько* файлов. Несколько - растяжимое понятие, поэтому было принято решение делать программу, готовую к *большому* (с некоторыми оговорками, о них ниже) количеству файлов. Так как количество возможных файловых дескрипторов в системе ограничено, их может не хватить на все файлы, переданные программе, поэтому был выбран вариант алгоритма, где одновременно будут открыты только 2 файла: выходной и один из входных. Можно было бы держать одновременно несколько открытых входных файлов, но затраты времени на их открытие и закрытие остались бы такими же, как если бы они открывались и закрывались последовательно, а не все вместе.

Чтение одной строки из всех файлов с последующим помещением данных из этих строк в массив (при условии, что такой массив влезет в ОЗУ), нахождение максимума из этого массива (нахождение максимума ~~=~~ сортировка, поэтому условия задачи позволяют его использовать), удаление этого элемента из массива и повторением процесса, пока массив не опустеет, что было бы достаточно быстро, не является правильным вариантом действия, потому что числа (или строки) **А** и **Б**, которые в выходном файле должны идти друг за другом, могут находится в одном входном файле.

Использование многопоточности для одновременной сортировки слиянием нескольких входных файлов, разделённых на группы, с последующим созданием промежуточных результатов сортировки, тоже не лучший вариант, потому что, если сами файлы могут не влезать в ОЗУ (что указано в задании), то результат сортировки слиянием нескольких таких файлов тем более не влезет в ОЗУ (если, конечно, все эти файлы будут содержать *хорошие* строки - нормальные числа в случае чисел и строки без пробелов в случае строк). Значит, для промежуточных файлов тоже нужно место на диске. Если входные файлы занимают хотя бы 50% дискового пространства, то промежуточные файлы займут оставшиеся 50%, места на выходной файл попросту не останется.

Принимая во внимание всё вышеперечисленное, были сделаны некоторые допущения:
1. Предполагается, что в ОЗУ поместится строка с аргументами для программы, массив объектов FilePortion примерно такой же длины и объект ExecutionParameters, содержащий в себе массив примерно такой же длины, не говоря уже об остальных объектах, которые нужны для работы программы, размер которых, однако, не зависит от количества входных файлов.
Работа только с частью входных файлов - плохой вариант, потому что некоторая информация о частично обработанных файлах (например, на какой строке закончили чтение в предыдущий раз) должна храниться (поэтому переиспользовать массив объектов для хранения такой информации не получится) и по причине, описанной для многопоточности.
2. другие фривольности, которые делают работу программы хотя бы теоретически возможной (например, то, что входные файлы занимают меньше 50% дискового пространства и что файловая система может хранить файлы огромных размеров).

## A rough translation for those who do not speak the language above

Java version - Oracle OpenJDK version 17.0.2

### Execution parameters

1. sorting type (optional) - ascending or descending (respectively)
-a or -d
2. data type (obligatory) - string or integer (respectively)
-s or -i
3. output file name (obligatory)
4. input file names (obligatory, at least one)


### Here is some reasoning for why the programme is the way it is:

Task specifies being ready for **SOME** number of files -> there may not be enough file descriptors for all of them -> we need use limited number of them (1 for output, 1 for one of the inputs, functionally identical to 1 for output *N* for input).

Reading 1 good line from all files, putting them into array (provided, such an array will fit in RAM), finding max (not sorting, just max), removing that element and starting over until no more elements in array, which would have been relatively fast, is not viable since numbers **a** and **b** that should be one after the other in the output file may come from the same input file.

Using threads to simultaneously sort files in groups of *N* (total / number of threads) is not viable as the result of such sorting will be greater (provided files only contain good lines, which may be the case) than any of the input files, hence, according to specification of the task, may not fit in RAM, thus needing a temporary file to store it. That, however, may be problematic since if the combined size of input files exceeds 50% of total disk space, the combined size of temp files will also exceed 50% of total disk space, making creation of desired output file impossible due to lack of aforementioned space.

With all that in mind, certain frivolities were made:
1. We presume that RAM can fit an array of strings for command line arguments, an array of FilePortion objects of similar length and an ExecutionParameters object, that also contains an array of length close to args, not to mention other objects, the size of which does not depend on the number of input files.
Working only a portion of input files is not viable because certain information about every file need to be stored (thus making reusing an array of *FileInfo or smth* objects of a fixed size impossible) and because the same reason as for multithreading.
2. other frivolities that make the execution at least theoretically possible (such as input files taking less than 50% of available disk space and file system being able to store files of great volume).
