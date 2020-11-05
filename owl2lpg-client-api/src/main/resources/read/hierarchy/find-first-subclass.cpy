MATCH (:Class {iri:$entityIri})<-[:SUB_CLASS_OF]-(n)
RETURN n
LIMIT 1