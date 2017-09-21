module.exports = (server, router) => {
    
    server.post('/items', (req, res) => {
        const db = router.db    
        const items = router.db.get('items').value()
        res.jsonp(items)
    })
    
    server.get('/items/:id', (req, res) => {
        const db = router.db
        const item = router.db.get('items').find({ 'Identifier': req.params.id }).value()
        res.jsonp(item)
    })
}