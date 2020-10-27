CALL db.index.fulltext.createNodeIndex("annotation_assertion_index",["Literal"],["lexicalForm"], { analyzer: "webprotege-analyzer" });
CALL db.index.fulltext.createNodeIndex("local_name_index",["Entity"],["localName"], { analyzer: "webprotege-analyzer" });
CALL db.index.fulltext.createNodeIndex("prefixed_name_index",["Entity"],["prefixedName"], { analyzer: "webprotege-analyzer" });
CALL db.index.fulltext.createNodeIndex("obo_id_index",["Entity"],["oboId"], { analyzer: "webprotege-analyzer" });