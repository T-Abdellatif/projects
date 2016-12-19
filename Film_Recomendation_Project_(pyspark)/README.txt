this project is still under development and will be executed on Jupyter
to use this project you will need :
	- docker
	- pyspark
	- Jupyter


to have these requirement run in your terminal:
	- docker run -p 8888:8888 -p 4040:4040 -v $(pwd)/notebook:/home/jovyan/work agarg0/pyspark2-notebook:latest 
	- docker run -p 8888:8888 -p 4040:4040 -v $(pwd)/notebook:/home/jovyan/work -e GRANT_SUDO=yes --user root ezamir/jupyter-spark-2.0:latest


then run in file containing "notebook" :
	- docker run -p 8888:8888 -p 4040:4040 -v $(pwd)/notebook:/home/jovyan/work agarg0/pyspark2-notebook:latest 
	- and in firefox : http://localhost:8888/tree
