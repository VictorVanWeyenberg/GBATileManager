#!/bin/bash

path=""
for f in $(find target -name "*.jar"); do
	path="$path:$f"
done
module_path="/home/cnpuvache/Downloads/javafx-sdk-19/lib/"
modules="javafx.controls,javafx.fxml"
main_class="dev.cnpuvache.gba.tile_manager.StartUp"

java \
	-cp "$path" \
	--module-path $module_path \
	--add-modules $modules \
		$main_class
	# -Dprism.verbose=true \
