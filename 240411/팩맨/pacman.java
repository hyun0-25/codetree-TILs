import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 시작: 00:24
 * 종료:
 * 
 * 문제해석
 * 4X4 격자에 m개 몬스터, 1개 팩맨
 * 각 몬스터는 상하좌우대각선 8방 중 하나
 * 
 * 1. 몬스터 복제 시도
 * 	현재의 위치에서 자신과 같은 방향을 가진 몬스터를 복제
 * 		복제된 몬스터는 아직 부화X==이동X
 * 2. 몬스터 이동
 * 	몬스터는 현재 자신이 가진 방향대로 1칸 이동
 * 		움직이려는 칸에 몬스터 시체 또는 팩맨 또는 격자밖-> 반시계 45도 회전 반복
 * 			갈 수 있는 방향X-> 이동X
 * 3. 팩맨 이동
 * 	총 3칸 이동
 * 		각 이동마다 상하좌우 4방 선택지(상좌하우 순)
 * 		4^3=64개의 선택지
 * 		몬스터를 가장 많이 먹는 방향으로 움직임
 * 			먹은 칸에는 몬스터 시체
 * 			알은 안먹음, 움직이기 전에 함께 있던 몬스터도 안먹음
 * 				-> 즉 이동과정에 있는 몬스터만 먹음
 * 4. 몬스터 시체 소멸
 * 	몬스터 시체는 2턴동안만 유지
 * 5. 몬스터 복제 완성
 * 	알 형태의 몬스터 부화
 * 		복제된 몬스터의 방향을 가진 채 부화
 * 
 * 
 * 입력
 * 첫째줄: 몬스터 마리수 m, 진행턴수t
 * 둘째줄: 팩맨 초기위치 r,c
 * m개줄: 몬스터 위치 r,c,방향d 1~8
 * 
 * 출력
 * 모든 턴 진행 뒤 살아 남은 몬스터의 마리 수 출력
 * 
 * 문제해결프로세스
 * 1. 몬스터 복제
 * 	map[][] 상태 복제
 * 2. 몬스터 이동
 * 	map[][]의 몬스터들 1칸이동(반시계45도 회전, 8방불가시 이동x+방향유지)
 * 		몬스터시체,팩맨,격자밖==이동불가
 * 3. 팩맨이동
 * 	64경우의수 -> 상좌하우 순
 * 		몬스터의 수 max
 * 			방향 i,j,k 저장
 * 	방향대로 이동
 * 		map[][] 비우기 + die[][]=time+2;
 * 4. 시체 소멸
 * 	x
 * 5. 복제 완성
 * 	1에서 복제한 map[][].add
 * 
 * 
 * 
 * 시간복잡도
 * 
 */
public class Main {
	static int m,t,px,py,time;
	static int die[][];
	static List<Monster>[][] map,copymap,newmap;
	static class Monster{
		int x,y,d;

		public Monster(int x, int y, int d) {
			super();
			this.x = x;
			this.y = y;
			this.d = d;
		}

		@Override
		public String toString() {
			return "Monster [x=" + x + ", y=" + y + ", d=" + d + "]";
		}
	}
	static int dx[] = {-1,-1,0,1,1,1,0,-1};
	static int dy[] = {0,-1,-1,-1,0,1,1,1};
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		m = Integer.parseInt(st.nextToken());
		t = Integer.parseInt(st.nextToken());
		map = new List[4][4];
		copymap = new List[4][4];
		die = new int[4][4];
		st = new StringTokenizer(br.readLine());
		px = Integer.parseInt(st.nextToken())-1;
		py = Integer.parseInt(st.nextToken())-1;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				map[i][j] = new ArrayList<>();
			}
		}
		for (int i =0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken())-1;
			int y = Integer.parseInt(st.nextToken())-1;
			int d = Integer.parseInt(st.nextToken())-1;
			map[x][y].add(new Monster(x,y,d));
		}
		for (int i = 1; i <= t; i++) {
			time = i;
			copymonster();
			movemonster();
			movepacman();
			addmonster();
		}
		//결과출력
		int result = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result+=map[i][j].size();
			}
		}
		System.out.println(result);
	}
	 /* 문제해결프로세스
	 * 1. 몬스터 복제
	 * 	map[][] 상태 복제
	 * 2. 몬스터 이동
	 * 	map[][]의 몬스터들 1칸이동(반시계45도 회전, 8방불가시 이동x+방향유지)
	 * 		몬스터시체,팩맨,격자밖==이동불가
	 * 3. 팩맨이동
	 * 	64경우의수 -> 상좌하우 순
	 * 		몬스터의 수 max
	 * 			방향 i,j,k 저장
	 * 	방향대로 이동
	 * 		map[][] 비우기 + die[][]=time+2;
	 * 4. 시체 소멸
	 * 	x
	 * 5. 복제 완성
	 * 	1에서 복제한 map[][].add
	 */
	public static void addmonster() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				map[i][j].addAll(copymap[i][j]);
			}
		}
	}
	public static void movepacman() {
		int max = Integer.MIN_VALUE;
		int pi = -1;
		int pj = -1;
		int pk = -1;
		boolean visited[][] = new boolean[4][4];
		for (int i = 0; i < 8; i+=2) {
			int xi = px+dx[i];
			int yi = py+dy[i];
			if(!rangecheck(xi,yi)) continue;
			int size1 = map[xi][yi].size();
			for (int j = 0; j < 8; j+=2) {
				int xj = xi+dx[j];
				int yj = yi+dy[j];
				if(!rangecheck(xj,yj)) continue;
				int size2 = map[xj][yj].size();
				for (int k = 0; k < 8; k+=2) {
					int xk = xj+dx[k];
					int yk = yj+dy[k];
					if(!rangecheck(xk,yk)) continue;
					int size3 = map[xk][yk].size();
					
					visited = new boolean[4][4];
					if(visited[xi][yi]) {
						size1=0;
					}
					visited[xi][yi]=true;
					if(visited[xj][yj]) {
						size2=0;
					}
					visited[xj][yj]=true;
					if(visited[xk][yk]) {
						size3=0;
					}
					visited[xk][yk]=true;
					int size = size1+size2+size3;
					if(size>max) {
						max = size;
						pi=i;
						pj=j;
						pk=k;
					}
				}
			}
		}
		
		int xi = px+dx[pi];
		int yi = py+dy[pi];
		if(map[xi][yi].size()>0) {
			die[xi][yi] = time+2;
			map[xi][yi].clear();
		}
		int xj = xi+dx[pj];
		int yj = yi+dy[pj];
		if(map[xj][yj].size()>0) {
			die[xj][yj] = time+2;
			map[xj][yj].clear();
		}
		int xk = xj+dx[pk];
		int yk = yj+dy[pk];
		if(map[xk][yk].size()>0) {
			die[xk][yk] = time+2;
			map[xk][yk].clear();
		}
		//팩맨 위치조정
		px = xk;
		py = yk;
	}
	
	public static void movemonster() {
		newmap = new List[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				newmap[i][j] = new ArrayList<>();
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < map[i][j].size(); k++) {
					Monster m = map[i][j].get(k);
					int x = m.x;
					int y = m.y;
					int d = m.d;
					int cnt = 0;
					while(cnt<8) {
						int nx = x+dx[d];
						int ny = y+dy[d];
						if(!rangecheck(nx,ny) || (nx==px && ny==py) || die[nx][ny]>=time) {
							cnt++;
							d++;
							if(d>=8) d-=8;
						}
						else {
							newmap[nx][ny].add(new Monster(nx,ny,d));
							break;
						}
					}
					if(cnt==8) {
						newmap[i][j].add(m);
					}
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				map[i][j] = new ArrayList<>(newmap[i][j]);
			}
		}
	}
	
	public static void copymonster(){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				copymap[i][j] = new ArrayList<>(map[i][j]);
			}
		}
	}
	public static boolean rangecheck(int rx, int ry) {
		return rx>=0 && rx<4 && ry>=0 && ry<4;
	}

}