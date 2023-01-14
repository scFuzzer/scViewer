# scViewer
A control flow graph & call graph builder for java smart contracts.

## About The Project

**中山大学InplusLab**与**远光软件YGSoft**合作开发的智能合约理解工具，旨在利用程序分析技术从Java智能合约中提取图结构信息，这其中主要包含了两点: (1)构建控制流图; (2)构建函数调用图。

# Getting Started

目前本项目基于Soot进行开发，将两个功能分别以两个模块实现，并打包成jar包，方便直接使用。

## 环境配置

需要配置如下环境
```
sudo apt install openjdk-11-jdk
sudo apt install graphviz
cp ./dependencies/rt.jar /usr/lib/jvm/java-11-openjdk-amd64/lib/
```

## Build from the source (optional)

我们已经将打包好的jar包放在根目录，若希望重新编译打包，可使用以下代码。
```
cd soot
mvn package assembly:single
cp target/sootclasses-trunk-jar-with-dependencies.jar ./..
```

## Build from docker (optional)

我们已经将打包好的docker镜像放在根目录并配置好环境，可以直接导入镜像使用。
```
docker import - scviewer < scViewer.tar
cd root/scViewer
```

# Usage

## Usage: CallGraphBuilder

指定java类，构建函数调用图，并生成dot文件于SootOutput。

```
java -cp /path/to/jar soot.tools.CallGraphBuilder className /path/to/target/classes

# demo
java -cp ./sootclasses-trunk-jar-with-dependencies.jar soot.tools.CallGraphBuilder com.example.App ./demo/target/classes
```

## Usage: CFGBuilder

指定java类，构建控制流图，并生成多个dot文件于sootOutput。

```
java -cp /path/to/jar soot.tools.CFGBuilder className /path/to/target/classes

# demo
java -cp ./sootclasses-trunk-jar-with-dependencies.jar soot.tools.CFGBuilder com.example.GCD ./demo/target/classes
```

## Usage: Graphviz

将所生成的dot文件转换为png。
```
dot -Tpng -o /path/to/arbitraryName.png /path/to/dot

# demo
dot -Tpng -o ./sootOutput/callgraph.png ./sootOutput/CallGraph.dot
```
