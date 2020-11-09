CALL apoc.import.csv(
   [
      {fileName: 'file:/$directoryName/project.csv', labels: []},
      {fileName: 'file:/$directoryName/branch.csv', labels: []},
      {fileName: 'file:/$directoryName/ontology-documents.csv', labels: []},
      {fileName: 'file:/$directoryName/axioms.csv', labels: []},
      {fileName: 'file:/$directoryName/cardinality-expressions.csv', labels: []},
      {fileName: 'file:/$directoryName/entities.csv', labels: []},
      {fileName: 'file:/$directoryName/anonymous-individuals.csv', labels: []},
      {fileName: 'file:/$directoryName/literals.csv', labels: []},
      {fileName: 'file:/$directoryName/iris.csv', labels: []},
      {fileName: 'file:/$directoryName/other-nodes.csv', labels: []}
   ], [
      {fileName: 'file:/$directoryName/related-to-edges.csv', type: ''},
      {fileName: 'file:/$directoryName/next-edges.csv', type: ''},
      {fileName: 'file:/$directoryName/structural-edges.csv', type: ''},
      {fileName: 'file:/$directoryName/augmenting-edges.csv', type: ''}
   ], {
      ignoreDuplicateNodes: true
   }
)