
appdir {
	root = 'dev'
	covers = appdir.root + '/covers'
	resources = 'src/main/resources'
}

h2 {
	driver = 'org.h2.Driver'
	url = 'jdbc:h2:file:dev/db/music'
	username = 'SA'
	password = ''
	showSql = false
}

server {
	port = 8080
	resource.base = 'web/app/'
}

test {
	num = 123
	string = "abc"
	floats = 4.567
	bool = true
	nil = null
	chars = 'c'
	date = new Date()
	arr = [1,2,3]
}

coverart {
	nocover = "no-cover.svg"
}
