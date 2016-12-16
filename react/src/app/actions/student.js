export function create(student) {
    return () => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if(Math.random() > 0.4) {
                    resolve(student);
                } else {
                    reject({ error: 'Server error' });
                }
            }, 750);
        });
    };
}
